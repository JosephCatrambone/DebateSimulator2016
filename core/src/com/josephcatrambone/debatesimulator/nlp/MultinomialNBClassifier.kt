package com.josephcatrambone.debatesimulator.nlp

import java.io.Serializable
import kotlin.math.*

class MultinomialNBClassifier(val numFeatures:Int, val numClasses:Int, val alpha:Float=1.0f) : Serializable {

	// P(a|b) = P(b|a)*P(a) /
	// Probability word i occurs in document from class C = p(w_i | C)
	// Probability doc contains all words w given class C = p(D|C) = PROD{ p(w_i | C) }
	// Probability doc is in class C?  P(C|D)
	// P(A intersect B) = P(B)P(A|B) = P(A)P(B|A)
	// P(D|C) = P(D intersect C) / P(C)  and P(C|D) = P(C /| D) / P(D)
	// Therefore: P(C|D) = P(C)P(D|C) / P(D)
	// If C = spam and there are two classes, T, F.
	// P(D|S) = PROD{ P(w_i | S) }
	// P(D|not S) = PROD{ P(w_i | not S) }
	// So, using Bayes' rule.
	// P(S|D) = ( P(S)/P(D) ) * PROD{ P(w_i | S) }
	// and
	// P(not S|D) = ( P(not S)/P(D) ) * PROD{ i for all P(w_i | not S) }
	// And dividing one by the other gives...
	// P(S|D)/P(not S|D) = ( P(S)/P(not S) ) * PROD{ P(w_i | S) / P(w_i | not S) }

	// For Multinomial NB,
	// p(x|Ck) = (sum{ x_i }! / prod{ x_i }!) * prod{ p_ki^xi },
	// but since we have one class, we can reduce p(x|Ck) to prod{ p_ki }

	/*
	class MultinomialNB(object):
    def __init__(self, alpha=1.0):
        self.alpha = alpha

    def fit(self, X, y):
        count_sample = X.shape[0]
        separated = [[x for x, t in zip(X, y) if t == c] for c in np.unique(y)]
        # Separated[0] has now the examples for class 0.  Separated[1] is an array of examples for class 1.
        self.class_log_prior_ = [np.log(len(i) / count_sample) for i in separated]
        # class_log_prior is an array of floating-point values of len #classes.  Probably negative.
        count = np.array([np.array(i).sum(axis=0) for i in separated]) + self.alpha
        # This sums up the values of all the examples for the given class and adds alpha to prevent zero-values.
        # The output shape of count is (#classes x #features)
        self.feature_log_prob_ = np.log(count / count.sum(axis=1)[np.newaxis].T)
        # Divide each element in each row in count by the sum of the row.
        # feature_log_prob is shape (#class x #features).
        return self

    def predict_log_proba(self, X):
        return [(self.feature_log_prob_ * x).sum(axis=1) + self.class_log_prior_
                for x in X]

    def predict(self, X):
        return np.argmax(self.predict_log_proba(X), axis=1)
	 */

	var totalExamples = 0
	val classCount = IntArray(numClasses)
	//val notClassCount = IntArray(numClasses)
	val classLogPrior = FloatArray(numClasses) // log(classCount / totalExamples)
	val featureLogProbability = Array(numClasses, { i -> FloatArray(numFeatures) })

	fun fit(examples: Array<FloatArray>, labels: IntArray) {
		assert(examples.size == labels.size)
		totalExamples = examples.size

		for(i in 0 until totalExamples) {
			val example = examples[i]
			val label = labels[i]

			classCount[label] += 1
			for (featureIndex in 0 until numFeatures) {
				featureLogProbability[label][featureIndex] += example[featureIndex]
			}
		}

		for(classIndex in 0 until numClasses) {
			for(featureIndex in 0 until numFeatures) {
				featureLogProbability[classIndex][featureIndex] += alpha
			}

			// Calculate the class-level log prior.  log(percent of total samples that are in this class)
			classLogPrior[classIndex] = ln(classCount[classIndex].toDouble() / totalExamples.toDouble()).toFloat()

			// Calculate the feature-level probability.
			val rowSum = featureLogProbability[classIndex].sum().toDouble()
			for(featureIndex in 0 until numFeatures) {
				featureLogProbability[classIndex][featureIndex] = ln(featureLogProbability[classIndex][featureIndex].toDouble() / rowSum).toFloat()
			}
		}
	}

	fun predict(example: FloatArray): Int {
		val preds = probabilities(example)
		var maxIndex = 0
		var maxValue = preds[0]
		for(i in 1 until numClasses) {
			if(preds[i] > maxValue) {
				maxValue = preds[i]
				maxIndex = i
			}
		}
		return maxIndex
	}

	fun probabilities(example: FloatArray): FloatArray {
		return FloatArray(numClasses, { i ->
			featureLogProbability[i].zip(example).fold(classLogPrior[i], {acc, (featLogProb, exampleValue) -> acc + (featLogProb*exampleValue)})
		})
	}
}
