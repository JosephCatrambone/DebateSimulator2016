package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

class Debate : Scene() {
	override fun render() {
		Gdx.gl.glClearColor(1f, 0f, 1f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
	}

	override fun update(delta: Float) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun dispose() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}