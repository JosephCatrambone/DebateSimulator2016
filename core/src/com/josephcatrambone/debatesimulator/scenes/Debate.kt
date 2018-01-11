package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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
	val proctor = Image(GDXMain.TEXTURE_ATLAS.findRegion("proctor"))
	val player = Image(GDXMain.TEXTURE_ATLAS.findRegion("hillary"))
	val trump = Image(GDXMain.TEXTURE_ATLAS.findRegion("trump"))
	val text = TextArea("", skin) // Use text.text, not text.messageText
	val timer = ProgressBar(0f, 1f, 0.01f, false, skin)
	var currentCharacter = 0 // used when gradually showing the text.
	var timeToNextCharacter = 0f

	// Gameplay elements.
	var playerSkipKey = false // Player has requested a skip.
	val proctorQuestions = mutableListOf<String>() // Use this to track what questions are asked.
	val playerResponses = mutableListOf<String>()
	val trumpResponses = mutableListOf<String>()
	var playerResponding = false // If both of these are false, the proctor is asking a question.
	var trumpResponding = false

	init {
		// If proctor is a sprite:
		//proctor.setRegion()
		//proctor.setBounds(0f, 0f, 128f, 128f)
		// If proctor is an image:
		//player.drawable = TextureRegionDrawable()

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

		proctor.setPosition(stage.width/2, PROCTOR_DEFOCUS_Y, Align.center)
		proctor.setAlign(Align.center)
		stage.addActor(proctor)

		makeProctorActive("If you were a hotdog and you were starving, would you eat yourself?")
	}

	fun makeTrumpActive(response:String) {
		trumpResponding = true
		trumpResponses.add(response)
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

	fun makeProctorActive(question:String) {
		trumpResponding = false
		playerResponding = false
		proctorQuestions.add(question)
		// Fade out both contestants and tween in the proctor.
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, DEFOCUS_ALPHA, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, DEFOCUS_ALPHA, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_ACTIVE_Y, { f -> proctor.y = f }))
		text.isDisabled = true
		text.text = ""
	}

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
				makeProctorActive("This is another fucking prompt.")
			}
		} else if(playerResponding) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				// Finish and submit our response.
				// Now we go to the player or the proctor.
				makeTrumpActive(trumpAI.generateReply(Topic.JOBS))
			}
		} else { // Being prompted.
			val s = proctorQuestions.last()
			val done = updateTextDisplay(delta, s)
			if(done) {
				makePlayerActive()
			}
		}
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