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

class Debate : Scene() {
	val TRANSITION_TIME = 0.6f
	val DEFOCUS_ALPHA = 0.3f
	val PROCTOR_ACTIVE_Y = 480f-256 //stage.height*5/8
	val PROCTOR_DEFOCUS_Y = -300f

	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))

	val table = Table(skin)
	val proctor = Image(GDXMain.TEXTURE_ATLAS.findRegion("proctor"))
	val player = Image(GDXMain.TEXTURE_ATLAS.findRegion("hillary"))
	val trump = Image(GDXMain.TEXTURE_ATLAS.findRegion("trump"))
	val text = TextArea("", skin)
	val timer = ProgressBar(0f, 1f, 0.01f, false, skin)

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
	}

	fun makeTrumpActive() {
		// Fade out the player, fade in Trump, move away the proctor.
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, DEFOCUS_ALPHA, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, 1.0f, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_DEFOCUS_Y, { f -> proctor.y = f }))
		text.isDisabled = true
		text.messageText = "Trump: "
	}

	fun makePlayerActive() {
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, 1.0f, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, DEFOCUS_ALPHA, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_DEFOCUS_Y, { f -> proctor.y = f }))
		text.isDisabled = false
		text.messageText = ""
	}

	fun makeProctorActive() {
		// Fade out both contestants and tween in the proctor.
		TweenManager.add(BasicTween(TRANSITION_TIME, player.color.a, DEFOCUS_ALPHA, { f -> player.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, trump.color.a, DEFOCUS_ALPHA, { f -> trump.setColor(1.0f, 1.0f, 1.0f, f)}))
		TweenManager.add(BasicTween(TRANSITION_TIME, proctor.y, PROCTOR_ACTIVE_Y, { f -> proctor.y = f }))
		text.isDisabled = true
		text.messageText = "Proctor: "
	}

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.draw()
	}

	override fun update(delta: Float) {
		Gdx.input.inputProcessor = stage
		stage.act(delta)

		if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			makePlayerActive()
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
			makeTrumpActive()
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			makeProctorActive()
		}
	}

	override fun dispose() {
		stage.dispose()
		skin.dispose()
	}
}