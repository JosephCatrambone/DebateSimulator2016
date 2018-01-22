package com.josephcatrambone.debatesimulator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.TextArea

class TextFeed(val text: TextArea, val textSpeedDivisor:Float, val debounceTime: Float = 0.5f) {
	var timeToNextCharacter = 0f
	var playerSkipKey = false
	var currentCharacter = 0
	var debounceTimeRemaining = debounceTime

	fun updateTextDisplay(delta: Float, s: String): Boolean {
		// Update the time.
		timeToNextCharacter -= delta
		if(timeToNextCharacter <= 0f) {
			currentCharacter++

			text.text = s.substring(0, minOf(s.length, currentCharacter))
			timeToNextCharacter = GDXMain.PREFERENCES.getInteger("TEXT_SPEED") / textSpeedDivisor
		}
		debounceTimeRemaining -= delta

		// Pick a prompt at random.
		//if(currentCharacter > proctorQuestions.last().length)
		// If the key is down, speed through the rest of the characters.  If released, go to next.
		if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && debounceTimeRemaining < 0f) {
			playerSkipKey = true
			timeToNextCharacter = 0f
			currentCharacter = s.length
		} else if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && playerSkipKey) {
			// Finish the text display process.
			playerSkipKey = false
			currentCharacter = 0
			debounceTimeRemaining = debounceTime
			return true
		}

		return false
	}
}