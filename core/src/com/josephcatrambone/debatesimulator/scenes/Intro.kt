package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.josephcatrambone.debatesimulator.GDXMain

class Intro : Scene() {

	val batch = SpriteBatch()

	val xoanaTexture = Texture(Gdx.files.internal("xoana.png"))
	val awfulJamsTexture = Texture(Gdx.files.internal("awfuljams.png"))
	val awfulWinterJam = Texture(Gdx.files.internal("awj2018.png"))
	val spaghetti = Texture(Gdx.files.internal("spaghetti.png"))
	val weirdHistory = Texture(Gdx.files.internal("weird_history.png"))
	val textures = arrayOf(xoanaTexture, awfulJamsTexture, awfulWinterJam, spaghetti, weirdHistory)

	var textureIndex = 0
	var fadeDelay = 0f
	var fadeAmount = 0f
	var fadeDirection = 1f

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		val tex = textures[textureIndex]

		batch.setColor(1f, 1f, 1f, fadeAmount)
		batch.begin()
		batch.draw(tex, widthCenter(tex), heightCenter(tex))
		batch.end()
	}

	private fun widthCenter(tex:Texture) = Gdx.graphics.width/2f - tex.width/2f
	private fun heightCenter(tex:Texture) = Gdx.graphics.height/2f - tex.height/2f

	override fun update(delta: Float) {
		// If any key is pressed, jump to the next display.
		if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
			batch.setColor(1f, 1f, 1f, 0f)
			fadeAmount = 0f
			fadeDirection = 1f

			textureIndex += 1
			if(textureIndex >= textures.size) {
				nextScreen()
				return
			}
		}

		// Stay visible for a spell.
		if(fadeAmount >= 1f && fadeDirection == 1f) {
			// Start fade out?
			if(fadeDelay < 0.99f) { // Start counting up.
				fadeAmount = 1.0f
				fadeDelay += delta
			} else { // We've been visible long enough.  Fade out.
				fadeDirection = -1f
				fadeAmount = 1.0f
				fadeDelay = 0f
			}
		} else if(fadeAmount <= 0f && fadeDirection == -1f) {
			// We're faded out.  No delay.  Start fade in of next picture.
			// Advance the image or, if there are no more to display, load title.
			textureIndex += 1
			if(textureIndex >= textures.size) {
				nextScreen()
				return
			}

			// Fade in.
			fadeAmount = 0f
			fadeDirection = 1f
		} else {
			fadeAmount += fadeDirection*delta
		}
	}

	fun nextScreen() {
		textureIndex = 0
		fadeDelay = 0f
		fadeAmount = 0f
		fadeDirection = 1f

		GDXMain.ACTIVE_SCENE = GDXMain.MAIN_MENU_SCENE
	}

	override fun dispose() {
		batch.dispose()
		textures.forEach { t -> t.dispose() }
	}
}