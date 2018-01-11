package com.josephcatrambone.debatesimulator.nlp

import java.util.*

class TrumpGenerator() {
	val topicToCFG = mutableMapOf<Topic,ContextFreeGrammar>()

	init {
		// Trump generator CSV format:
		// topic,match,rule (pipe delimited or all on different lines?)
	}

	fun generateReply(topic:String): String {
		val sb = StringBuilder()
		sb.append(topic)
		while(random.nextInt(5) != 1) {
			sb.append(" ")
			sb.append(topic)
		}
		return sb.toString()
	}
}
