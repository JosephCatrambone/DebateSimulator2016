package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.debatesimulator.EaseTween
import com.josephcatrambone.debatesimulator.GDXMain
import com.josephcatrambone.debatesimulator.GDXMain.PREFERENCES
import com.josephcatrambone.debatesimulator.TweenManager
import kotlin.math.E
import kotlin.math.roundToInt

class MainMenu : Scene() {
	val TRANSITION_TIME = 1.0f
	val EASE = 0.5f
	val INACTIVE_Y:Float = 1280f
	val ACTIVE_Y:Float

	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))

	val backdrop = Image(GDXMain.TEXTURE_ATLAS.findRegion("menubg"))

	// Main menu.
	val mainMenuTable = Table()
	val newGameButton = TextButton("New Game", skin)
	val newGamePlusButton = TextButton("New Game+", skin)
	val optionsButton = TextButton("Options", skin)
	val creditsButton = TextButton("Credits", skin)
	val quitButton = TextButton("Quit", skin)
	val backButton1 = TextButton("Back", skin)
	val backButton2 = TextButton("Back", skin)

	// Options menu
	val textSpeedSlider = Slider(0.0f, 10f, 1.0f, false, skin)

	val optionsTable = Table()

	val creditsTable = Table()

	init {
		stage.addActor(backdrop)

		// Set up main menu.
		mainMenuTable.add(newGameButton)
		mainMenuTable.row()
		if(false) {
			mainMenuTable.add(newGamePlusButton)
			mainMenuTable.row()
		}
		mainMenuTable.add(optionsButton)
		mainMenuTable.row()
		mainMenuTable.add(creditsButton)
		mainMenuTable.row()
		mainMenuTable.add(quitButton)
		mainMenuTable.row()

		mainMenuTable.setFillParent(true)
		stage.addActor(mainMenuTable)

		// Set up options menu.
		optionsTable.add(Label("Text Speed: ", skin))
		optionsTable.add(Label("(Slow) ", skin))
		optionsTable.add(textSpeedSlider)
		optionsTable.add(Label(" (Fast)", skin))
		optionsTable.row()
		optionsTable.add(backButton1)
		optionsTable.add()
		optionsTable.add()
		optionsTable.add()
		optionsTable.row()

		optionsTable.setFillParent(true)
		stage.addActor(optionsTable)

		// Credits menu
		creditsTable.add(Label("Code:", skin))
		creditsTable.row()
		creditsTable.add(Label("Joseph Catrambone - @jcatrambone", skin))
		creditsTable.row()
		creditsTable.add()
		creditsTable.row()
		creditsTable.add(Label("Proctor, Clinton, Trump:", skin))
		creditsTable.row()
		creditsTable.add(Label("Peter Queckenstedt - @scutanddestroy", skin))
		creditsTable.row()
		creditsTable.add()
		creditsTable.row()
		creditsTable.add(Label("Campaign manager stolen from Afal.  Sorry. :(", skin))
		creditsTable.row()
		creditsTable.add(backButton2)
		creditsTable.row()

		creditsTable.setFillParent(true)
		stage.addActor(creditsTable)

		ACTIVE_Y = mainMenuTable.y

		setUpButtonBehaviors()
		showMainMenu()
	}

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		stage.draw()
	}

	override fun update(delta: Float) {
		Gdx.input.inputProcessor = stage
		stage.act(delta)
	}

	override fun dispose() {
		stage.dispose()
	}

	fun setUpButtonBehaviors() {
		newGameButton.addListener(object : ChangeListener() {
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				startNewGame()
			}
		})

		newGamePlusButton.addListener(object : ChangeListener() {
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				startNewGame(true)
			}
		})

		optionsButton.addListener(object : ChangeListener(){
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				showOptions()
			}
		})

		creditsButton.addListener(object : ChangeListener(){
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				showCredits()
			}
		})

		quitButton.addListener(object : ChangeListener(){
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				// TODO: Save?  Prompt?
				Gdx.app.exit()
			}
		})

		val backListener = object : ChangeListener() {
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				showMainMenu()
			}
		}
		backButton1.addListener(backListener)
		backButton2.addListener(backListener)

		// Options
		optionsButton.addListener(object : ChangeListener() {
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				showOptions()
			}
		})

		textSpeedSlider.addListener(object : ChangeListener() {
			override fun changed(event: ChangeEvent?, actor: Actor?) {
				actor as Slider
				PREFERENCES.putInteger("TEXT_SPEED", 10-actor.value.roundToInt())
			}
		})
	}

	fun startNewGame(newGamePlus:Boolean = false) {
		// Change the 'new game' button to 'Continue'.
		newGameButton.setText("Continue")
		// TODO: Fade out.
		GDXMain.ACTIVE_SCENE = GDXMain.BRIEFING_SCENE
		GDXMain.BRIEFING_SCENE.refocused()
		// We do NOT pop the main menu because they may want to come back here to adjust options.
	}

	fun showMainMenu() {
		// Hide the options and the credits tables.  Show main menu table.
		TweenManager.add(EaseTween(TRANSITION_TIME, mainMenuTable.y, ACTIVE_Y, EASE, {f -> mainMenuTable.y = f}))
		TweenManager.add(EaseTween(TRANSITION_TIME, optionsTable.y, INACTIVE_Y, EASE, {f -> optionsTable.y = f}))
		TweenManager.add(EaseTween(TRANSITION_TIME, creditsTable.y, INACTIVE_Y, EASE, {f -> creditsTable.y = f}))
	}

	fun showOptions() {
		// Hide the main menu table.  Show the options table.
		TweenManager.add(EaseTween(TRANSITION_TIME, mainMenuTable.y, INACTIVE_Y, EASE, {f -> mainMenuTable.y = f}))
		TweenManager.add(EaseTween(TRANSITION_TIME, optionsTable.y, ACTIVE_Y, EASE, {f -> optionsTable.y = f}))
		TweenManager.add(EaseTween(TRANSITION_TIME, creditsTable.y, INACTIVE_Y, EASE, {f -> creditsTable.y = f}))
	}

	fun showCredits() {
		TweenManager.add(EaseTween(TRANSITION_TIME, mainMenuTable.y, INACTIVE_Y, EASE, {f -> mainMenuTable.y = f}))
		TweenManager.add(EaseTween(TRANSITION_TIME, optionsTable.y, INACTIVE_Y, EASE, {f -> optionsTable.y = f}))
		TweenManager.add(EaseTween(TRANSITION_TIME, creditsTable.y, ACTIVE_Y, EASE, {f -> creditsTable.y = f}))
	}
}