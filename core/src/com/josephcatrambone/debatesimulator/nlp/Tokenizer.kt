package com.josephcatrambone.debatesimulator.nlp

import java.io.InputStream
import java.io.Serializable
import java.util.*

class Tokenizer : Serializable {
	companion object {
		private val serialVersionUid: Long = 282480326079L
	}


	val indexToWord: Array<String>
	val wordToIndex: Map<String,Int>

	val numTokens:Int
		get() { return indexToWord.size }

	constructor(inStream: InputStream) {
		// Load dictionary from words.txt.
		val fin = Scanner(inStream);

		val dictionary = ArrayList<String>();
		while(fin.hasNext()) {
			dictionary.add(fin.next());
		}
		this.indexToWord = dictionary.toTypedArray();

		wordToIndex = HashMap<String, Int>()
		for ((i, w) in indexToWord.iterator().withIndex()) {
			wordToIndex[w] = i;
		}
	}

	constructor(sentences: Array<String>) {
		val words = mutableSetOf<String>()
		sentences.forEach({ s ->
			words.addAll(split(s))
		})

		indexToWord = words.toTypedArray()
		wordToIndex = mapOf<String, Int>(*indexToWord.mapIndexed { index, s -> Pair(s, index) }.toTypedArray())
	}

	fun split(sentence: String): Array<String> {
		// Convert to lower case and remove punct.  Then take off the last open space (where punctuation was).
		val msg = sentence.toLowerCase().replace("""[\W]""".toRegex(), " ").trimEnd();

		// Split into tokens.
		val tokens = msg.split(' ').filter { c -> !c.equals("") };

		// TODO: Replace groups of tokens like ['i' 'm'] with 'i' 'am' or 'he' 'd' with 'he' 'would'.
		return tokens.toTypedArray()
	}

	fun tokenize(words: Array<String>): IntArray {
		// Given a bunch of words, look them up and produce an array of integers.
		// Iterate over tokens and check for special classes like '1st', numbers, and names.
		val res = mutableListOf<Int>()
		words.forEach { w ->
			if(wordToIndex.containsKey(w)) {
				res.add(wordToIndex[w]!!)
			}
		}
		return res.toIntArray()
	}

	fun vectorize(tokens: IntArray): FloatArray {
		val fa = FloatArray(indexToWord.size)
		tokens.filter { t -> t >= 0 && t < indexToWord.size }.forEach { t -> fa[t] += 1.0f }
		return fa
	}

	fun run(sentence:String): FloatArray {
		return vectorize(tokenize(split(sentence)))
	}

	fun run(sentences: Array<String>): Array<FloatArray> {
		return sentences.map { sentence -> vectorize(tokenize(split(sentence))) }.toTypedArray()
	}
}
