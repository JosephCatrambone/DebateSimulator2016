package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.debatesimulator.*
import com.josephcatrambone.debatesimulator.nlp.*
import com.opencsv.CSVReader

class Debate : Scene() {
	val CHARACTER_DEBOUNCE = 10 // There must be at least this many character printed by the prompt before advancing.
	val TEXT_SPEED_DIVISOR = 100f // We read PREFERENCES.getInteger("TEXT_SPEED") and divide by this for the chardelay.
	val TRANSITION_TIME = 0.6f
	val DEFOCUS_ALPHA = 0.3f
	val PROCTOR_ACTIVE_Y = 480f-256 //stage.height*5/8
	val PROCTOR_DEFOCUS_Y = -300f

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
	val timer = ProgressBar(0f, 1f, 0.01f, false, skin)
	var currentCharacter = 0 // used when gradually showing the text.
	var timeToNextCharacter = 0f
	val playerFeedbackLabelStart = Pair(-50f, 480f-150f)
	val playerFeedbackLabelEnd = Pair(100f, playerFeedbackLabelStart.second) // Only X motion.

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

		// Table layout:
		// player | bars | trump
		// huge text area
		// Timer bar
		table.add(player)
		table.add()
		table.add(trump)
		table.row()
		table.add(text).colspan(3).expand().fill()
		table.row()
		table.add(timer).colspan(3).expandX().fillX()

		table.setFillParent(true)
		stage.addActor(table)

		// Load proctor questions.
		loadPrompts()

		// Fire first question.
		proctor.setPosition(stage.width/2, PROCTOR_DEFOCUS_Y, Align.center)
		proctor.setAlign(Align.center)
		stage.addActor(proctor)

		// Initial prompt.
		val prompt = prompts.removeAt(0)
		lastPromptTopic = prompt.first
		proctorQuestions.add(prompt.second)
		makeProctorActive()
	}

	fun loadPrompts() {
		val reader = CSVReader(Gdx.files.internal("questions.csv").reader())
		val entries = reader.readAll()
		entries.forEach({ topic_question ->
			prompts.add(Pair(Topic.valueOf(topic_question[0]), topic_question[1]))
		})
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

		if(trumpResponding) {
			val s = trumpResponses.last()
			val done = updateTextDisplay(delta, s)
			if(done) {
				// Trump has finished his thing.  Select a new prompt at random.
				val prompt = prompts.removeAt(0)
				lastPromptTopic = prompt.first
				proctorQuestions.add(prompt.second)
				makeProctorActive()
			}
		} else if(playerResponding) {
			// Ready to submit?
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				// Finish and submit our response.
				// TODO: Do we want to split this by sentence?
				val (demo, change) = GDXMain.ELECTON_SCENE.updateAllDemographics(text.text)

				// Show user feedback: popup with demographic and delta.
				showDemographicFeedbackPopup(demo, change)

				// Now we go to the player or the proctor.
				trumpResponses.add(trumpAI.generateReply(lastPromptTopic))
				makeTrumpActive()
			}
		} else { // Being prompted.
			val s = proctorQuestions.last()
			val done = updateTextDisplay(delta, s)
			if(done) {
				makePlayerActive()
			}
		}
	}

	private fun showDemographicFeedbackPopup(demo: String, change: Float) {
		// Create a label, add it to the actors, create a tween where it pops up, play a sound.
		var deltaText = "$demo: "
		var popupColor = Color(0.1f, 0.1f, 0.1f, 1f)

		if(change > 0) {
			deltaText += "+$change"
			popupColor = Color(0.1f, 1.0f, 0.1f, 1.0f)
		} else if(change < 0) {
			deltaText += "$change"
			popupColor = Color(1.0f, 0.1f, 0.1f, 1.0f)
		} else {
			println("DEBUG: Statement did not change demographics.  What the fuck?")
			return // Bail early if there's no difference.
		}

		// TODO: Add font name and color.
		val label = Label(deltaText, skin)
		label.style.fontColor = popupColor
		label.setPosition(playerFeedbackLabelStart.first, playerFeedbackLabelStart.second)
		stage.addActor(label)
		TweenManager.add(SequentialTween(
			// Slide in.
			EaseTween(1f, 0f, 1f, 0.5f, { t ->
				label.setPosition(
					playerFeedbackLabelEnd.first*t + playerFeedbackLabelStart.first*(1f-t),
					playerFeedbackLabelEnd.second*t + playerFeedbackLabelStart.second*(1f-t)
				)
			}),
			// Fade out.
			EaseTween(1f, 1f, 0f, 0.5f, { t ->
				val col = label.color
				label.setColor(col.r, col.g, col.b, t)
			}),
			// Remove from stage.
			DelayTween(1f, {
				label.remove()
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