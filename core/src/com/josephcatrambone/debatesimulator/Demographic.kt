package com.josephcatrambone.debatesimulator

import com.josephcatrambone.debatesimulator.nlp.MultinomialNBClassifier
import com.josephcatrambone.debatesimulator.nlp.Tokenizer
import java.io.Serializable

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

	fun updateSentiment(statements:List<String>): Float { // Returns net change in sentiment.  0 -> 1 really dislike to really like.
		val startSentiment = sentimentTowardsPlayer

		var deltaSentiment = 0.0f
		var dislikeAccumulator = 0.0f
		var likeAccumulator = 0.0f
		val tokens = tokenizer.run(statements.toTypedArray())
		val preds = tokens.map { tok -> likePlayerClassifier.probabilities(tok) }

		// TODO: Maybe we should take the max sentiment and not the average sentiment.
		preds.forEach({sentimentClasses ->
			dislikeAccumulator += sentimentClasses[0]
			likeAccumulator += sentimentClasses[1]
		})
		// Roll it all together using some maths.
		dislikeAccumulator /= preds.size
		likeAccumulator /= preds.size

		// Half old sentiment.  Half new.
		val newSentiment = sentimentTowardsPlayer*0.5f + (-dislikeAccumulator + likeAccumulator)*0.5f
		deltaSentiment = newSentiment - startSentiment
		sentimentTowardsPlayer = newSentiment
		return deltaSentiment
	}
}
