package com.josephcatrambone.debatesimulator.nlp

import com.badlogic.gdx.Gdx
import com.josephcatrambone.debatesimulator.Topic
import java.util.*

class TrumpGenerator() {
	val cfg = ContextFreeGrammar(Gdx.files.internal("trump.csv").read())
	val topicToStartToken = mapOf(
		Topic.ABORTION to "#ABORTION",
		Topic.BUDGET_AND_ECONOMY to "#BUDGET#",
		Topic.JOBS to "#JOBS#"
	)

	init {
		// Trump generator CSV format:
		// topic,match,rule (pipe delimited or all on different lines?)
	}

	fun generateReply(topic:Topic): String {
		var resp = ""
		do {
			resp = cfg.generateString(topicToStartToken.getOrDefault(topic, "#S#"))
		} while(resp.length < 1)
		return resp
	}
}
