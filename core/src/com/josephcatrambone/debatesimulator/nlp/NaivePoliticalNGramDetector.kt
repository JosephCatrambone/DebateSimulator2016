package com.josephcatrambone.debatesimulator.nlp

class NaivePoliticalNGramDetector {
	// Political Ideology Detection Using Recursive Neural Networks (Iyyer et al. 2014)
	// From the paper http://www.aclweb.org/anthology/P/P14/P14-1105.pdf
	val conservative = listOf(
		"salt", "mexico", "housework", "speculated", "consensus", "lawyer", "pharmaceuticals", "ruthless", "deadly", "clinton", "redistribution",
		"prize individual liberty", "original liberal idiots", "stock market crash", "god gives freedom", "federal government interference", "federal oppression nullification", "respect individual liberty", "tea party patriots", "radical sunni islamists", "obama stimulus programs",
		"spending on popular government programs", "bailouts and unfunded government promises", "north america from external threats", "government regulations place on businesses", "strong Church of Christ convictions", "radical Islamism and other threats",
		"government intervention helped make the depression great", "by god", "in his image and likeness",  "producing wealth instead of stunting capital creation", "the traditional american values of limited government", "trillions of dollars to overseas oil producers", "its troubled assets to federal sugar daddies", "obama and his party as racialist fanatics"
	)

	val liberal = listOf(
		"rich", "antipsychotic", "malaria", "biodiversity", "richest", "gene", "pesticides", "desertification", "net", "wealthiest", "labor", "fertilizer", "nuclear", "hiv",
		"rich and poor", "corporate greed", "super rich pay", "carrying the rich", "corporate interest groups", "young women workers", "the very rich", "for the rich", "by the rich", "soaking the rich", "getting rich often", "great and rich", "the working poor", "corporate income tax", "the poor migrants",
		"the rich are really rich", "effective forms of worker participation", "the pensions of the poor", "tax cuts for the rich", "the ecological services of biodiversity", "poor children and pregnant women", "vacation time for overtime pay",
		"african americans and other disproportionately poor groups", "the growing gap between rich and poor", "the Bush tax cuts for the rich", "public outrage at corporate and societal greed", "sexually transmitted diseases most notably AIDS", "organize unions or fight for better conditions", "the biggest hope for health care reform"
	)

	val conservativeRegex = listOf( // List of regexes of increasing strength -- less likely to match and more likely to indicate conservative.  Position in list indicates confidence.
		Regex("clinton", RegexOption.IGNORE_CASE),
		Regex("second ammendment", RegexOption.IGNORE_CASE),
		Regex("personal freedom", RegexOption.IGNORE_CASE),
		Regex("radical islam", RegexOption.IGNORE_CASE),
		Regex("welfare state", RegexOption.IGNORE_CASE),
		Regex("create jobs", RegexOption.IGNORE_CASE),
		Regex("under god", RegexOption.IGNORE_CASE),
		Regex("bureaucracy", RegexOption.IGNORE_CASE),
		Regex("government (over)?regulation", RegexOption.IGNORE_CASE),
		Regex("red tape", RegexOption.IGNORE_CASE),
		Regex("government interference", RegexOption.IGNORE_CASE)
	)

	val liberalRegex = listOf(
		Regex("bush(-era)?\stax cuts( for the rich)?", RegexOption.IGNORE_CASE),
		Regex("systemic racism", RegexOption.IGNORE_CASE),
		Regex("freedom (of|to)? (choice|choose)?", RegexOption.IGNORE_CASE),
		Regex("black lives matter", RegexOption.IGNORE_CASE),
		Regex("income inequality", RegexOption.IGNORE_CASE)
	)

	/*** classifySentiment
	* Returns a pair of (-1 for left liberal and +1 for right conservative) and confidence (0 for not confident 1 for certain).
	* Goes through the list and tries to match the conservative angle.  For each hit, increases the strength based on the location of the regex in the list.
	* Then goes through the liberal list.  For each hit, increases the strength based on the location of the regex.
	* Then compares the hits.  If it's one sided, confidence is high.  If it's not, confidence is low.  
	*/
	fun classifySentiment(s:String): Pair(Float, Float) {
		
	}
}
