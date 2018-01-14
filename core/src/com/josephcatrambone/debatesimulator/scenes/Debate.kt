package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.debatesimulator.*
import com.josephcatrambone.debatesimulator.nlp.*
import com.opencsv.CSVReader

class Debate : Scene() {
	val RESPONSE_TIME = 60f
	val CHARACTER_DEBOUNCE = 10 // There must be at least this many character printed by the prompt before advancing.
	val TEXT_SPEED_DIVISOR = 100f // We read PREFERENCES.getInteger("TEXT_SPEED") and divide by this for the chardelay.
	val TRANSITION_TIME = 0.6f
	val DEFOCUS_ALPHA = 0.3f
	val PROCTOR_ACTIVE_Y = 480f-256 //stage.height*5/8
	val PROCTOR_DEFOCUS_Y = -300f
	val PLAYER_FEEDBACK_LABEL_START = Pair(-50f, 480f-150f)
	val PLAYER_FEEDBACK_LABEL_END = Pair(100f, PLAYER_FEEDBACK_LABEL_START.second) // Only X motion.
	val FEEDBACK_DROP_SHADOW_DISTANCE = 2f

	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))

	// NLP components.
	val trumpAI = TrumpGenerator()

	// Display things.
	val table = Table(skin)
	val backdrop = Image(GDXMain.TEXTURE_ATLAS.findRegion("backdrop"))
	val proctor = Image(GDXMain.TEXTURE_ATLAS.findRegion("proctor"))
	val player = Image(GDXMain.TEXTURE_ATLAS.findRegion("hillary"))
	val trump = Image(GDXMain.TEXTURE_ATLAS.findRegion("trump"))
	val text = TextArea("", skin) // Use text.text, not text.messageText
	val timer = ProgressBar(0f, RESPONSE_TIME, 0.01f, false, skin)
	var currentCharacter = 0 // used when gradually showing the text.
	var timeToNextCharacter = 0f

	// Gameplay elements.
	var playerSkipKey = false // Player has requested a skip.
	val prompts = mutableListOf<Pair<Topic, String>>()
	val proctorQuestions = mutableListOf<String>() // Use this to track what questions are asked.
	val playerResponses = mutableListOf<String>()
	val trumpResponses = mutableListOf<String>()
	var lastPromptTopic: Topic = Topic.JOBS
	var playerResponding = false // If both of these are false, the proctor is asking a question.
	var trumpResponding = false

	init {
		// If proctor is a sprite:
		//proctor.setRegion()
		//proctor.setBounds(0f, 0f, 128f, 128f)
		// If proctor is an image:
		//player.drawable = TextureRegionDrawable()

		// HACK: Lazy shit maneuver: just drop the backdrop actor at 0,0 because that's where the root of the scene is
		// and, coincidentally, it will align with the players.
		backdrop.setPosition(0f, stage.height/2f, Align.bottomLeft)
		stage.addActor(backdrop)

		//val scrollpane = ScrollPane(text, skin)

		// Table layout:
		// player | bars | trump
		// huge text area
		// Timer bar
		table.add(player)
		table.add()
		table.add(trump)
		table.row()
		table.add(text).colspan(3).expand().fill() // TODO: Scroll pane with autoscroll.
		table.row()
		table.add(timer).colspan(3).expandX().fillX()

		//text.setPrefRows(4f)

		table.setFillParent(true)
		stage.addActor(table)
		table.layout()
		text.layout()

		// Fire first question.
		proctor.setPosition(stage.width/2, PROCTOR_DEFOCUS_Y, Align.center)
		proctor.setAlign(Align.center)
		stage.addActor(proctor)
		stage.setKeyboardFocus(text)

		startDebate("questions_night_1.csv")
	}

	fun startDebate(questionFile:String) {
		// Initialize player variables.
		prompts.clear()
		proctorQuestions.clear()
		playerResponses.clear()
		trumpResponses.clear()

		// Load questions for this set.
		val reader = CSVReader(Gdx.files.internal(questionFile).reader())
		val entries = reader.readAll()
		entries.forEach({ topic_question ->
			prompts.add(Pair(Topic.valueOf(topic_question[0]), topic_question[1]))
		})

		// Initial prompt.
		val prompt = prompts.removeAt(0)
		lastPromptTopic = prompt.first
		proctorQuestions.add(prompt.second)
		makeProctorActive()
	}

	fun endDebateSession() {
		GDXMain.ACTIVE_SCENE = GDXMain.BRIEFING_SCENE
		GDXMain.BRIEFING_SCENE.refocused()
	}

	// START: These are all ui methods.  Don't use them for control logic.
	fun makeTrumpActive() {
		trumpResponding = true
		// Fade out the player, fade in Trump, move away the proctor.
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, DEFOCUS_ALPHA, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, 1.0f, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_DEFOCUS_Y, { f -> proctor.y = f }))
		text.isDisabled = true
		text.text = ""
	}

	fun makePlayerActive() {
		playerResponding = true
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, 1.0f, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, DEFOCUS_ALPHA, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_DEFOCUS_Y, { f -> proctor.y = f }))
		text.isDisabled = false
		text.text = ""
	}

	fun makeProctorActive() {
		trumpResponding = false
		playerResponding = false
		// Fade out both contestants and tween in the proctor.
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, DEFOCUS_ALPHA, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, DEFOCUS_ALPHA, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_ACTIVE_Y, { f -> proctor.y = f }))
		text.isDisabled = true
		text.text = ""
	}
	// END: UI methods.

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.draw()
	}

	override fun update(delta: Float) {
		Gdx.input.inputProcessor = stage
		stage.act(delta)

		// TODO: This state code is a mess.  Needs to be cleaned up.
		// Proctor -> Player -> Trump
		//
		if(trumpResponding) {
			timer.value = timer.value - delta // TODO: Is this meaningful here?
			val s = trumpResponses.last()
			val done = updateTextDisplay(delta, s)
			if(done) {
				timer.value = RESPONSE_TIME
				// Trump has finished his thing.  Select a new prompt at random.
				val prompt = prompts.removeAt(0) // The last entry in the questions will be the end of the night.
				lastPromptTopic = prompt.first
				proctorQuestions.add(prompt.second)
				makeProctorActive()
			}
		} else if(playerResponding) {
			timer.value = timer.value - delta
			// Ready to submit?
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || timer.value <= 0f) {
				// Finish and submit our response.
				// TODO: Do we want to split this by sentence?
				val (demo, change) = GDXMain.ELECTON_SCENE.updateAllDemographics(text.text)
				playerResponses.add(text.text)

				// Show user feedback: popup with demographic and delta.
				showDemographicFeedbackPopup(demo, change)

				// Now we go to the player or the proctor.
				timer.value = RESPONSE_TIME
				trumpResponses.add(trumpAI.generateReply(lastPromptTopic))
				makeTrumpActive()
			}
		} else { // Proctor active.
			val s = proctorQuestions.last()
			val done = updateTextDisplay(delta, s)
			if(done) {
				// Are we out of questions?
				if(prompts.isEmpty()) {
					endDebateSession()
					return // Break early.
				}
				// Refil the timer.
				timer.value = RESPONSE_TIME
				makePlayerActive()
			}
		}
	}

	private fun showDemographicFeedbackPopup(demo: String, change: Float) {
		// Create a label, add it to the actors, create a tween where it pops up, play a sound.
		var deltaText = "$demo: "
		var popupColor = Color(0.1f, 0.1f, 0.1f, 1f)

		if(change > 0) {
			deltaText += "+%.2f".format(change)
			popupColor = Color(0.1f, 1.0f, 0.1f, 1.0f)
		} else if(change < 0) {
			deltaText += "%.2f".format(change)
			popupColor = Color(1.0f, 0.1f, 0.1f, 1.0f)
		} else {
			println("DEBUG: Statement did not change demographics.  What the fuck?")
			return // Bail early if there's no difference.
		}

		// TODO: This is full of gross style hacks involving cloning the style sheets.
		val label = Label(deltaText, skin)
		label.style = Label.LabelStyle(label.style) // Clone style.
		val labelBackdrop = Label(deltaText, skin) // HACK: For text outline/backdrop.  TODO: Text outline?
		labelBackdrop.style = Label.LabelStyle(label.style)
		label.style.fontColor = popupColor
		labelBackdrop.style.fontColor = Color(0f, 0f, 0f, 1f)
		// This throws off alignment, so change font size instead.
		//labelBackdrop.setFontScale(labelBackdrop.fontScaleX*1.1f, labelBackdrop.fontScaleY*1.1f)
		//labelBackdrop.scaleX = label.scaleX*1.1f
		label.setPosition(PLAYER_FEEDBACK_LABEL_START.first, PLAYER_FEEDBACK_LABEL_START.second)
		labelBackdrop.setPosition(PLAYER_FEEDBACK_LABEL_START.first, PLAYER_FEEDBACK_LABEL_START.second)
		stage.addActor(labelBackdrop)
		stage.addActor(label)
		TweenManager.add(SequentialTween(
			// Slide in.
			EaseTween(1f, 0f, 1f, 0.5f, { t ->
				val x = PLAYER_FEEDBACK_LABEL_END.first*t + PLAYER_FEEDBACK_LABEL_START.first*(1f-t)
				val y = PLAYER_FEEDBACK_LABEL_END.second*t + PLAYER_FEEDBACK_LABEL_START.second*(1f-t)
				labelBackdrop.setPosition(x+FEEDBACK_DROP_SHADOW_DISTANCE, y-FEEDBACK_DROP_SHADOW_DISTANCE)
				label.setPosition(x,y)
			}),
			// Fade out.
			BasicTween(5f, 1f, 0f, { t ->
				val col = label.color
				label.setColor(col.r, col.g, col.b, t*t) // Square fade for the text.
				labelBackdrop.setColor(col.r, col.g, col.b, t) // Linear fade for the backdrop
			}),
			// Remove from stage.
			DelayTween(0.1f, {
				label.remove()
				labelBackdrop.remove()
			})
		))
		// TODO: Play sound.
	}

	fun updateTextDisplay(delta:Float, s:String): Boolean {
		// Update the time.
		timeToNextCharacter -= delta
		if(timeToNextCharacter <= 0f || playerSkipKey) {
			currentCharacter++

			text.text = s.substring(0, minOf(s.length, currentCharacter))
			timeToNextCharacter = GDXMain.PREFERENCES.getInteger("TEXT_SPEED")/TEXT_SPEED_DIVISOR
		}

		// Pick a prompt at random.
		//if(currentCharacter > proctorQuestions.last().length)
		// If the key is down, speed through the rest of the characters.  If released, go to next.
		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && currentCharacter > CHARACTER_DEBOUNCE) {
			playerSkipKey = true
			timeToNextCharacter = 0f
		} else if(!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && playerSkipKey) {
			// Finish the text display process.
			playerSkipKey = false
			currentCharacter = 0
			return true
		}

		return false
	}

	override fun dispose() {
		stage.dispose()
		skin.dispose()
	}
}