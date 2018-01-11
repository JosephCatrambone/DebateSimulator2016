package com.josephcatrambone.debatesimulator.nlp

import java.util.*
import com.josephcatrambone.debatesimulator.*

/*** ContextFreeGrammar
 * Given an a map of production rules, the starting point of which is '',
 * generate a series of statements.
 */
class ContextFreeGrammar(val productionRules:Map<String, List<String>>) {
	val START_TOKEN:String = ""
	val random = Random()

	// TODO: Assert production rules contain start token.

	private fun randomChoice(options:List<String>):String {
		return options[random.nextInt(options.size)]
	}

	fun generateWordList(): List<String> {
		var words = listOf<String>(START_TOKEN)
		var replacementHappened = true;
		while(replacementHappened) {
			replacementHappened = false;
			val nextStep:List<String> = words.flatMap({ w ->
				if(productionRules.contains(w)) {
					replacementHappened = true
					randomChoice(productionRules[w]!!)
				} else {
					listOf<String>(w)
				}
			})
			words = nextStep
		}
		return words
	}

	fun generateString(): String {
		val sb = StringBuilder()
		sb.append(topic)
		while(random.nextInt(5) != 1) {
			sb.append(" ")
			sb.append(topic)
		}
		return sb.toString()
	}
}


