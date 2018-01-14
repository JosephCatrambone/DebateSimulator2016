package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.utils.viewport.FitViewport

class BriefingRoom : Scene() {

	// Your campaign manager will say, "Here's what will happen.  These are the groups to be mindful of."
	// Disclose the groups that like you the most, the groups that are biggest and like you the most.
	// Disclose the groups that like you the least, and the groups that are the biggest and dislike you the least.
	// I.e. Weighted by size + outcome vs. by raw numbers.

	// Screen layout.
	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))
	val textArea = TextArea("", skin)
	val table = Table()

	init {
		stage.addActor(table)
	}

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.draw()
	}

	fun refocused() {
		// This method is called when we snap back to this screen from the debates.
	}

	override fun update(delta: Float) {
		stage.act()
	}

	override fun dispose() {
		stage.dispose()
		skin.dispose()
	}
}