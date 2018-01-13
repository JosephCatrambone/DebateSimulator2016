package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.josephcatrambone.debatesimulator.Demographic
import java.io.ObjectInputStream
import kotlin.math.abs

class ElectionResults : Scene() {

	// Demographics
	val gunNuts:Demographic

	// List of all of them for iteration and scoring.
	val demographics:List<Demographic>

	init {
		gunNuts = loadDemographic("gunnuts.demographic")

		demographics = listOf(gunNuts)
	}

	fun loadDemographic(name:String):Demographic {
		val obInputStream = ObjectInputStream(Gdx.files.internal(name).read())
		return obInputStream.readObject() as Demographic
	}

	override fun render() {
		Gdx.gl.glClearColor(1f, 0f, 1f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
	}

	fun updateAllDemographics(statement:String): Pair<String,Float> {
		// Runs an update on all the demographics and gives the biggest change.
		var maxChange = 0f
		var maxDemographic = ""

		demographics.forEach({ dem ->
			val delta = dem.updateSentiment(listOf(statement))
			if(abs(delta) > abs(maxChange)) {
				maxChange = delta
				maxDemographic = dem.demographicName
			}
		})

		return Pair(maxDemographic, maxChange)
	}

	fun getApprovalRating(): Float {
		var approval = 0.0f
		demographics.forEach({ dem ->
			approval += dem.sentimentTowardsPlayer
		})
		return approval / demographics.size
	}

	override fun update(delta: Float) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun dispose() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}