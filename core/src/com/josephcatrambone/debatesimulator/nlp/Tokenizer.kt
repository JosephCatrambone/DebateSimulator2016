package com.josephcatrambone.debatesimulator.nlp

import java.io.Serializable

class Tokenizer : Serializable {
	val indexToWord: Array<String>
	val wordToIndex: HashMap<String,Int>

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
		
	}

	fun split(sentence: String): Array<String> {
		// Convert to lower case and remove punct.  Then take off the last open space (where punctuation was).
		val msg = sentence.toLowerCase().replace("""[\W]""".toRegex(), " ").trimEnd();

		// Split into tokens.
		val tokens = msg.split(' ').filter { c -> !c.equals("") };

		// TODO: Replace groups of tokens like ['i' 'm'] with 'i' 'am' or 'he' 'd' with 'he' 'would'.
		return tokens.toTypedArray()
	}

	fun tokenize(words: Array<String>, missingClassIndex:Int=0, numberClassIndex:Int=1): IntArray {
		// Given a bunch of words, look them up and produce an array of integers.
		// Iterate over tokens and check for special classes like '1st', numbers, and names.
		return words.map( { s ->
			if(s.toCharArray().all { c -> c.isDigit() }) {
				numberClassIndex
			//} else if(s.length > 2 && (s.endsWith("st") || s.endsWith("nd") || s.endsWith("rd") || s.endsWith("th")) && s.substring(0, s.length-2).all { c -> c.isDigit() }) {
			} else {
				if(this.wordToIndex.containsKey(s)) {
					wordToIndex[s]!!
				} else {
					missingClassIndex
				}
			}
		}).toIntArray()
	}
}
