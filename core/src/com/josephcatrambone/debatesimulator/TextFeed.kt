package com.josephcatrambone.debatesimulator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.TextArea

class TextFeed(val text: TextArea, val textSpeedDivisor:Float, val characterDebounceCount:Int = 5) {
	var timeToNextCharacter = 0f
	var playerSkipKey = false
	var currentCharacter = 0

	fun updateTextDisplay(delta: Float, s: String): Boolean {
		// Update the time.
		timeToNextCharacter -= delta
		if(timeToNextCharacter <= 0f) {
			currentCharacter++

			text.text = s.substring(0, minOf(s.length, currentCharacter))
			timeToNextCharacter = GDXMain.PREFERENCES.getInteger("TEXT_SPEED") / textSpeedDivisor
		}

		// Pick a prompt at random.
		//if(currentCharacter > proctorQuestions.last().length)
		// If the key is down, speed through the rest of the characters.  If released, go to next.
		if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && currentCharacter > characterDebounceCount) {
			playerSkipKey = true
			timeToNextCharacter = 0f
			currentCharacter = s.length
		} else if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && playerSkipKey) {
			// Finish the text display process.
			playerSkipKey = false
			currentCharacter = 0
			return true
		}

		return false
	}
}