package com.josephcatrambone.debatesimulator.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.josephcatrambone.debatesimulator.Demographic
import com.josephcatrambone.debatesimulator.GDXMain
import com.josephcatrambone.debatesimulator.UnitedState
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.util.*
import kotlin.math.abs

class ElectionResults : Scene() {
	val CHARACTER_DEBOUNCE = 10 // There must be at least this many character printed by the prompt before advancing.
	val TEXT_SPEED_DIVISOR = 100f // We read PREFERENCES.getInteger("TEXT_SPEED") and divide by this for the chardelay.

	val random = Random()
	var debugTemp = 0

	// Demographics
	val antivaxers:Demographic
	val conservatives:Demographic
	val dogs:Demographic
	val hipsters:Demographic
	val gunNuts:Demographic
	val liberals:Demographic

	// List of all of them for iteration and scoring.
	val demographics:List<Demographic>
	val demographicAllocationByState:Map<UnitedState,StateDemographicInfo> // Int is the number in that state.

	// Vote outcomes.
	var popularVotes = 0
	var electoralVotes = 0
	val stateOutcomeInPlayerFavor = mutableMapOf<UnitedState,Boolean>()

	// Our whole map.
	var stateMap:Texture? = null
	val stateMapImage = Image(GDXMain.TEXTURE_ATLAS.findRegion("map"))
	val electionResultColors = mutableMapOf<UnitedState,Color>()

	// Screen layout.
	val stage = Stage(FitViewport(640f, 480f))
	val skin = Skin(Gdx.files.internal("default_skin.json"))
	val table = Table()

	val textArea = TextArea("", skin)
	var timeToNextCharacter = 0f
	var playerSkipKey = false
	var currentCharacter = 0

	init {
		antivaxers = loadDemographic("antivax.demographic")
		conservatives = loadDemographic("conservative.demographic")
		dogs = loadDemographic("dogs.demographic")
		gunNuts = loadDemographic("gunnuts.demographic")
		hipsters = loadDemographic("hipsters.demographic")
		liberals = loadDemographic("liberal.demographic")

		demographics = listOf(
			antivaxers,
			conservatives,
			dogs,
			gunNuts,
			hipsters,
			liberals
		)

		demographicAllocationByState = loadStateInfo("state_demographics.csv")

		table.add(stateMapImage).fill()
		table.row()
		table.add(textArea).fillX().expandX().padLeft(20f).padRight(20f)
		table.row()
		table.setFillParent(true)
		table.layout()
		stage.addActor(table)
	}

	fun loadDemographic(name:String):Demographic {
		val obInputStream = ObjectInputStream(Gdx.files.internal(name).read())
		return obInputStream.readObject() as Demographic
	}

	fun loadStateInfo(name:String): Map<UnitedState,StateDemographicInfo> {
        //val parser = CSVParser(',')
		val res = mutableMapOf<UnitedState, StateDemographicInfo>()
        val reader = CSVReader(InputStreamReader(Gdx.files.internal(name).read()));
        var parsed = reader.readNext()
        while(parsed != null) {
			val info = StateDemographicInfo(parsed, demographics)
			res[info.state] = info
            //replacements!!.add(parsed[1])
            parsed = reader.readNext()
        }
		return res
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
			println("DEBUG: ${dem.demographicName} : $delta")
			if(abs(delta) > abs(maxChange)) {
				maxChange = delta
				maxDemographic = dem.demographicName
			}
		})

		return Pair(maxDemographic, maxChange)
	}

	fun getElectionResults(): Map<UnitedState,Int> { // State -> Int, negative being lost by X amount, otherwise positive by Y votes.
		val stddev = 0.2f
		val results = mutableMapOf<UnitedState,Int>()

		UnitedState.values().forEach { state ->
			var votesForPlayer = 0
			var votesForOpponent = 0
			// Assume an even spread based on the approval from the group.
			val stateInfo = demographicAllocationByState[state]!!
			demographics.forEach({ demographic ->
				val possibleDemographicVotes = stateInfo.population * stateInfo.demographicDistribution.getOrElse(demographic, { -> 0f })
				votesForPlayer += (possibleDemographicVotes * demographic.sentimentTowardsPlayer*(demographic.baseVotingLikelihood + random.nextFloat()*(demographic.baseVotingLikelihood*stddev))).toInt()
				votesForOpponent += (possibleDemographicVotes * (1.0f-demographic.sentimentTowardsPlayer)*(demographic.baseVotingLikelihood + random.nextFloat()*(demographic.baseVotingLikelihood*stddev))).toInt()
			})
			//
			results[state] = votesForPlayer-votesForOpponent
		}

		return results
	}

	fun getApprovalRating(): Float {
		var approval = 0.0f
		demographics.forEach({ dem ->
			approval += dem.sentimentTowardsPlayer
		})
		return approval / demographics.size
	}

	override fun update(delta: Float) {
		Gdx.input.inputProcessor = stage
		stage.act()

		debugTemp++
		if(debugTemp%100 == 0) {
			// Make a random map.
			val electionResults = mutableMapOf<UnitedState,Color>()
			electionResults[UnitedState.values()[(debugTemp/100)%50]] = when (random.nextInt(2)) {
				0 -> Color.BLUE
				1 -> Color.RED
				else -> {
					throw Exception("Election Results: This can't happen.  3 > 2")
				}
			}
			stateMap?.dispose()
			stateMap = colorMap(electionResults)
			stateMapImage.drawable = TextureRegionDrawable(TextureRegion(stateMap))
			stateMapImage.width = stateMap!!.width.toFloat()
			stateMapImage.height = stateMap!!.height.toFloat()
		}
	}

	override fun dispose() {
		stateMap?.dispose()
		skin.dispose()
	}

	// Copied and pasted from debate because I'm a lazy shit:
	fun updateTextDisplay(delta:Float, s:String): Boolean {
		// Update the time.
		timeToNextCharacter -= delta
		if(timeToNextCharacter <= 0f || playerSkipKey) {
			currentCharacter++

			textArea.text = s.substring(0, minOf(s.length, currentCharacter))
			timeToNextCharacter = GDXMain.PREFERENCES.getInteger("TEXT_SPEED")/TEXT_SPEED_DIVISOR
		}

		// Pick a prompt at random.
		//if(currentCharacter > proctorQuestions.last().length)
		// If the key is down, speed through the rest of the characters.  If released, go to next.
		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && currentCharacter > CHARACTER_DEBOUNCE) {
			playerSkipKey = true
			timeToNextCharacter = 0f
		} else if(!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && playerSkipKey) {
			// Finish the text display process.
			playerSkipKey = false
			currentCharacter = 0
			return true
		}

		return false
	}

	class StateDemographicInfo {
		val state:UnitedState
		val stateName:String
		val population:Int
		val electoralVotes:Int
		val demographicDistribution:Map<Demographic,Float>

		constructor(csvRow:Array<String>, demographics:List<Demographic>) {
			assert(csvRow.size == 3 + demographics.size)
			stateName = csvRow[0]
			state = UnitedState.valueOf(stateName.toUpperCase().filter { chr -> chr.isLetter() })
			population = csvRow[1].toInt()
			electoralVotes = csvRow[2].toInt()

			val demoMap = mutableMapOf<Demographic,Float>()
			demographics.forEachIndexed({ i, demo -> 
				demoMap[demo] = csvRow[3+i].toFloat()
			})
			demographicDistribution = demoMap
		}
	}
}
