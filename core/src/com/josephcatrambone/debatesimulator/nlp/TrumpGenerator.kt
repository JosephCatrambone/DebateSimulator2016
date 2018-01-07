package com.josephcatrambone.debatesimulator.nlp

import java.util.*

class TrumpGenerator(speeches:Array<String>) {
	val random = Random()

	init {

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