package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

class BriefingRoom : Scene() {

	// Your campaign manager will say, "Here's what will happen.  These are the groups to be mindful of."
	// Disclose the groups that like you the most, the groups that are biggest and like you the most.
	// Disclose the groups that like you the least, and the groups that are the biggest and dislike you the least.
	// I.e. Weighted by size + outcome vs. by raw numbers.

	override fun render() {
		Gdx.gl.glClearColor(1f, 0f, 1f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
	}

	override fun update(delta: Float) {

	}

	override fun dispose() {

	}
}