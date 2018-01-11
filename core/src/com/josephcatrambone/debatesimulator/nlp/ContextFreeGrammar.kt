package com.josephcatrambone.debatesimulator.nlp

import java.util.*
import com.josephcatrambone.debatesimulator.*
import com.opencsv.CSVParser
import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader

/*** ContextFreeGrammar
 * Given an a map of production rules, the starting point of which is '',
 * generate a series of statements.
 */
class ContextFreeGrammar {
	val random = Random()
	val productionRules:Map<String, List<String>>

	constructor(prodRules:Map<String, List<String>>) {
		productionRules = prodRules
	}

	constructor(csv: InputStream) {
		val prodRules = mutableMapOf<String, MutableList<String>>()
		//val parser = CSVParser(',')
		val reader = CSVReader(InputStreamReader(csv));
		var parsed = reader.readNext()
		while(parsed != null) {
			assert(parsed.size == 2)
			var replacements: MutableList<String>? = prodRules[parsed[0]]
			if (replacements == null) {
				replacements = mutableListOf<String>()
				prodRules[parsed[0]] = replacements
			}
			replacements!!.add(parsed[1])
			parsed = reader.readNext()
		}
		productionRules = prodRules.toMap()
	}

	// TODO: Assert production rules contain start token.

	private fun randomChoice(options:List<String>):String {
		return options[random.nextInt(options.size)]
	}

	fun generateString(startToken:String="#S#"): String {
		var words = startToken
		var replacementHappened = true;
		while(replacementHappened) {
			replacementHappened = false
			val tokens = words.split('#').filter { tok -> tok.length > 0 }
			val sb = mutableListOf<String>()
			tokens.forEach({ t ->
				if(productionRules.containsKey(t)) {
					sb.add(randomChoice(productionRules[t]!!))
					replacementHappened = true
				}  else {
					sb.add(t)
				}
			})
			words = sb.joinToString(separator = " ")
		}
		return words
	}
}


