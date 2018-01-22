package com.josephcatrambone.debatesimulator

import com.josephcatrambone.debatesimulator.nlp.MultinomialNBClassifier
import com.josephcatrambone.debatesimulator.nlp.Tokenizer
import java.io.Serializable
import kotlin.math.exp

class Demographic(
	val baseVotingLikelihood:Float, // The odds that a member of this group will case a vote.
	val tokenizer: Tokenizer,
	val likePlayerClassifier:MultinomialNBClassifier, // For the classifier, feature 0 should be dislike, 1 should be like.
	val demographicName:String,
	val demographicHelpText:String,
	var sentimentTowardsPlayer: Float = 0.5f // 0 = dislike.  1 = like.
) : Serializable {

	companion object {
		private val serialVersionUid: Long = 1678148210794L
	}

	fun updateSentiment(statement:String): Float { // Returns net change in sentiment.  0 -> 1 really dislike to really like.
		var deltaSentiment = 0.0f
		val tokens = tokenizer.run(statement)
		val preds = likePlayerClassifier.probabilities(tokens)

		if(tokens.sum() > 0) {
			val rawDislike = preds[0]
			val rawLike = preds[1]

			deltaSentiment = exp(rawLike) - exp(rawDislike)

			// Half old sentiment.  Half new.
			sentimentTowardsPlayer += deltaSentiment
		}
		return deltaSentiment
	}
}
