package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.josephcatrambone.debatesimulator.Demographic
import com.josephcatrambone.debatesimulator.GDXMain
import com.josephcatrambone.debatesimulator.UnitedState
import java.io.ObjectInputStream
import java.util.*
import kotlin.math.abs

class ElectionResults : Scene() {
	val random = Random()
	var debugTemp = 0

	// Demographics
	val gunNuts:Demographic

	// List of all of them for iteration and scoring.
	val demographics:List<Demographic>

	// Our whole map.
	var stateMap:Texture? = null
	val stateMapImage = Image()
	val stage = Stage()

	init {
		gunNuts = loadDemographic("gunnuts.demographic")

		demographics = listOf(gunNuts)

		stage.addActor(stateMapImage)
	}

	fun loadDemographic(name:String):Demographic {
		val obInputStream = ObjectInputStream(Gdx.files.internal(name).read())
		return obInputStream.readObject() as Demographic
	}

	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.draw()
	}

	fun colorMap(stateColors:Map<UnitedState, Color>, defaultColor:Color=Color.GRAY, backgroundColor:Color=Color.CLEAR):Texture {
		val bgColor = Color.rgba8888(backgroundColor)

		val greyMap = GDXMain.TEXTURE_ATLAS.findRegion("map")
		// Prepare for reads.
		if(!greyMap.texture.textureData.isPrepared) {
			greyMap.texture.textureData.prepare()
		}
		val mapReader = greyMap.texture.textureData.consumePixmap()
		val mapWriter = Pixmap(greyMap.regionWidth, greyMap.regionHeight, Pixmap.Format.RGBA8888)
		// For each pixel, map to color?  For each color, map to pixel?
		// Faster to iterate over the map and then look up in the State -> Color map.
		for(y in 0 until mapWriter.height) {
			for(x in 0 until mapWriter.width) {
				val rawColor = mapReader.getPixel(greyMap.regionX + x, greyMap.regionY + y)
				val color = Color(rawColor)
				val stateNum:Int = (color.b*255).toInt()
				// Is this a valid state color?
				if(color.r == 0f && color.g == 0f && color.a == 1f) {
					assert(stateNum >= 0 && stateNum < UnitedState.values().size)
					// Yes!  So look up the state.
					val state = UnitedState.values()[stateNum]
					// Does it have a color?  If so, color our resulting pixmap.
					if(stateColors.containsKey(state)) {
						mapWriter.drawPixel(x, y, Color.rgba8888(stateColors[state]!!))
					} else {
						mapWriter.drawPixel(x, y, Color.rgba8888(defaultColor))
					}
				} else {
					// Not a valid color.
					mapWriter.drawPixel(x, y, bgColor) // clear
				}
			}
		}

		// Finally, convert the pixmap to texture.
		val tex = Texture(mapWriter)
		return tex
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
		stage.act()

		debugTemp++
		if(debugTemp%100 == 0) {

			// Clear the state map if it's set.
			if(stateMap != null) { stateMap!!.dispose() }
			// Make a random map.
			val electionResults = mutableMapOf<UnitedState,Color>()
			UnitedState.values().forEach { s ->
				// Randomly assign red/blue.
				electionResults[s] = when (random.nextInt(2)) {
					0 -> Color.BLUE
					1 -> Color.RED
					else -> {
						throw Exception("Election Results: This can't happen.  3 > 2")
					}
				}
			}
			stateMap = colorMap(electionResults)
			stateMapImage.drawable = TextureRegionDrawable(TextureRegion(stateMap))
			stateMapImage.width = stateMap!!.width.toFloat()
			stateMapImage.height = stateMap!!.height.toFloat()
		}
	}

	override fun dispose() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}