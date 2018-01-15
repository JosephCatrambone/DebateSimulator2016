package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.debatesimulator.GDXMain

class MainMenu : Scene() {

	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))

	// Main menu.
	val mainMenuTable = Table()
	val newGameButton = TextButton("New Game", skin)
	val newGamePlusButton = TextButton("New Game+", skin)
	val optionsButton = TextButton("Options", skin)
	val creditsButton = TextButton("Credits", skin)
	val quitButton = TextButton("Quit", skin)

	val optionsTable = Table()


	init {
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

		setUpButtonBehaviors()
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
	}

	fun startNewGame(newGamePlus:Boolean = false) {
		// Change the 'new game' button to 'Continue'.
		newGameButton.setText("Continue")
		// TODO: Fade out.
		GDXMain.ACTIVE_SCENE = GDXMain.BRIEFING_SCENE
		GDXMain.BRIEFING_SCENE.refocused()
		// We do NOT pop the main menu because they may want to come back here to adjust options.
	}

	fun showOptions() {

	}

	fun showCredits() {

	}
}