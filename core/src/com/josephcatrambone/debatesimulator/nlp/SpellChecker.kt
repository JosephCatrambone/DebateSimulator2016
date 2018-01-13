package com.josephcatrambone.debatesimulator.nlp

class SpellChecker(val dictionary: Set<String>) {
	fun generateSwaps(word: String): ArrayList<String> {
		val words = ArrayList<String>();

		var chars = word.toCharArray();
		for(i in (0..word.length-2)) {
			// Swap the character with the one next to it.
			var temp = chars[i];
			chars[i] = chars[i+1];
			chars[i+1] = temp;

			// Construct word.
			val word = String(chars);

			// Append to the list.
			words.add(word);

			// Swap the letters back.
			temp = chars[i];
			chars[i] = chars[i+1];
			chars[i+1] = temp;
		}

		return words;
	}

	fun generateDeletes(word: String): ArrayList<String> {
		val words = ArrayList<String>();

		// If the word is too short, don't check it.
		if(word.length < 2) {
			return words;
		}

		for(i in (0..word.length-1)) {
			// Construct word.
			val word = word.substring(0, i) + word.substring(i+1, word.length);
			// Add to the list.
			words.add(word);
		}
		return words;
	}

	fun generateInserts(word: String): ArrayList<String> {
		val words = ArrayList<String>();
		for(i in (0..word.length)) {
			for(j in 'a'..'z') {
				// Construct word.
				val word = word.substring(0, i) + j + word.substring(i, word.length);
				// Add to the list.
				words.add(word);
			}
		}
		return words;
	}

	fun generateReplaces(word: String): ArrayList<String> {
		val words = ArrayList<String>();
		var chars = word.toCharArray();
		for(i in (0..word.length-1)) {
			// Swap the character with the one next to it.
			var temp = chars[i];
			for(j in 'a'..'z') {
				chars[i] = j;

				// Construct word.
				val word = String(chars);

				// Add to the list.
				words.add(word);
			}

			// Swap the letters back.
			chars[i] = temp;
		}
		return words;
	}

	/*** closestWord
	 * Given a word, spell check it and see if it's in the dictionary.
	 * We only do one step of checks, but in theory we could recursively check for more.
	 */
	fun closestWord(word: String): String {
		// If no edits required, return word as it.
		if(word in this.dictionary) {
			return word;
		}

		// Otherwise, corrupt it to varying extents and try again.
		for(word in generateSwaps(word)) {
			if(word in this.dictionary) {
				return word;
			}
		}

		for(word in generateDeletes(word)) {
			if(word in this.dictionary) {
				return word;
			}
		}

		for(word in generateReplaces(word)) {
			if(word in this.dictionary) {
				return word;
			}
		}

		for(word in generateInserts(word)) {
			if(word in this.dictionary) {
				return word;
			}
		}

		return "<UNK>";
	}

	fun spellCheck(tokens: Array<String>): Array<String> {
		val checkedTokens = ArrayList<String>()
		for(t in tokens) {
			checkedTokens.add(closestWord(t))
		}
		return checkedTokens.toTypedArray()
	}
}
