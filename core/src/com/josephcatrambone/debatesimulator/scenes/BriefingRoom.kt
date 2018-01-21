package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.debatesimulator.GDXMain
import com.josephcatrambone.debatesimulator.TextFeed

class BriefingRoom : Scene() {
	val TEXT_SPEED_DIVISOR = 200f // We read PREFERENCES.getInteger("TEXT_SPEED") and divide by this for the chardelay.

	// Your campaign manager will say, "Here's what will happen.  These are the groups to be mindful of."
	// Disclose the groups that like you the most, the groups that are biggest and like you the most.
	// Disclose the groups that like you the least, and the groups that are the biggest and dislike you the least.
	// I.e. Weighted by size + outcome vs. by raw numbers.

	// Screen layout.
	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))
	val table = Table()

	val textArea = TextArea("", skin)
	val textStreamer = TextFeed(textArea, TEXT_SPEED_DIVISOR)

	// Graphics assets.
	val mgr = Image(GDXMain.TEXTURE_ATLAS.findRegion("campaign_manager"))

	var linesToDeliver = mutableListOf<String>()
	var lastDebate = 0

	init {
		table.add(mgr)
		table.add()
		table.row()
		table.add(textArea).expand().fill()
		table.setFillParent(true)

		stage.addActor(table)
	}

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.draw()
	}

	fun refocused() {
		// TODO: Maybe in each of our scenes we can just track when we gave away focus instead of requiring others to call 'refocus'.

		// This method is called when we snap back to this screen from the debates.
		if(lastDebate == 0) {
			// This is the first time we're in the briefing room.
			linesToDeliver.add("Hey, it's me.  Your campaign manager.")
			linesToDeliver.add("You know the drill.  The proctor will ask a question, you've got two minutes to answer.")
			linesToDeliver.add("Our market and demographic research has pinpointed a few groups to be aware of:")
			linesToDeliver.add(GDXMain.ELECTON_SCENE.gunNuts.demographicName + ": " + GDXMain.ELECTON_SCENE.gunNuts.demographicHelpText)
			linesToDeliver.add(GDXMain.ELECTON_SCENE.hipsters.demographicName + ": "+ GDXMain.ELECTON_SCENE.hipsters.demographicHelpText)
			linesToDeliver.add("Remember the time limit, try to wait for the whole question to finish, and don't feel obligated to stay on topic.  Your opponent won't.")
		} else if(lastDebate == 1 || lastDebate == 2) {
			linesToDeliver.add("Well done out there.")
			if(GDXMain.ELECTON_SCENE.getApprovalRating() > 0.5) {
				linesToDeliver.add("Your polling numbers are above average.")
			} else {
				linesToDeliver.add("Your polling numbers are below average.")
			}
			val supporters = GDXMain.ELECTON_SCENE.getBiggestSupporters()
			linesToDeliver.add("Right now, the demographic that's most supportive of you is ${supporters.demographicName}.")
			if(supporters.sentimentTowardsPlayer < 0.5) {
				linesToDeliver.add("But they still hate you.")
			}
			val detractors = GDXMain.ELECTON_SCENE.getBiggestDetractors()
			linesToDeliver.add("Your biggest detractors are ${detractors.demographicName}")
			if(detractors.sentimentTowardsPlayer > 0.5) {
				linesToDeliver.add("But they don't hate you that much.")
			} else {
				linesToDeliver.add("Remember, ${detractors.demographicHelpText}")
			}
			if(lastDebate < 3) {
				linesToDeliver.add("You've still got ${3 - lastDebate} debates left.")
			}
		} else {
			// We're out of debates.  Go to the election results.
			GDXMain.ACTIVE_SCENE = GDXMain.ELECTON_SCENE
		}
		lastDebate++ // Note: We do it here so we have 'night_1' and such.  Works with the 'done' check in update().
	}

	override fun update(delta: Float) {
		Gdx.input.inputProcessor = stage
		val done = textStreamer.updateTextDisplay(delta, linesToDeliver.first())
		stage.act()

		if(done) {
			// We're done reading this section.
			linesToDeliver.removeAt(0) // Pop first.
			if(linesToDeliver.isEmpty()) {
				// We've got nothing left to say.  First debate.
				GDXMain.ACTIVE_SCENE = GDXMain.DEBATE_SCENE
				GDXMain.DEBATE_SCENE.startDebate("questions_night_$lastDebate.csv")
			}
		}
	}

	override fun dispose() {
		stage.dispose()
		skin.dispose()
	}
}