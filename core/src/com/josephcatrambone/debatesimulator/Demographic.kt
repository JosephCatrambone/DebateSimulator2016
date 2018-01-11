package com.josephcatrambone.debatesimulator

enum class Topic {
	ABORTION,
	BUDGET_AND_ECONOMY,
	CIVIL_RIGHTS,
	CORPORATIONS,
	CRIME,
	DRUGS,
	EDUCATION,
	ENERGY_AND_OIL,
	ENVIRONMENT,
	FAMILIES_AND_CHILDREN,
	FOREIGN_POLICY,
	FREE_TRADE,
	GOVERNMENT_REFORM,
	GUN_CONTROL,
	HEALTHCARE,
	HOMELAND_SECURITY,
	IMMIGRATION,
	INFRASTRUCTURE_AND_TECHNOLOGY,
	JOBS,
	PRINCIPLES_AND_VALUES,
	SOCIAL_SECURITY,
	TAX_REFORM,
	WAR_AND_PEACE,
	WELFARE_AND_POVERTY 
}

data class Sentiment(val summary:String, val topics:Set<Topic>)

data class Standing(val name:String, val left:String, val right:String, val sentiments:Map<Sentiment, Float>)
// Example: 
/*
val financialStanding = Standing(
	"Social Standing", 
	"Socially Liberal", "Socially Conservative", 
	mapOf(
		Sentiment("Healthcare is a fundamental human right.", HEALTHCARE) to -1, // Agree = 1.0.  1 * -1 (from this mapping) = -1.  -1 = left.
		Sentiment("Homosexuality is a choice.", PRINCIPLES_AND_VALUES) to 1, // Agree = 1.0 -> 1 * 1 = 1.  1 = Right.
		Sentiment(
	)
)
// TODO: Map the player's input strings to a -1 or 1 range for disagree to agree for each sentiment.
*/

class Demographic(
	val populationSize:Int = 0,
	val baseVotingLikelihood:Float = 0.5f, // The odds that a member of this group will case a vote.
	val issueImportance:Map<Sentiment, Float> = mapOf<Sentiment, Float>(), // Sentiment -> 0, 1, // 0 - not important.  1 = super important.
	val issueAgreement:Map<Sentiment, Float> = mapOf<Sentiment, Float>() // Sentiment -> [-1, 1] // Disagree, agree.
) {
	fun getFondness(candidate:CandidateModel): Float {
		// Basic dot product.
		// If the candidate HASN'T spoken about a very important issue, this will decrease their fondness, but only for one issue.
		// We do this by selecting the most important issue for this group.
		var maxAgreement = 0f // While we're iterating, also sum the max agreement possible.
		//var (wedgeSentiment, wedgeValue) = issueImportance.entries.first()
		issueImportance.forEach({ _, value ->
			maxAgreement += value
		})

		// For each key/value, do the dot between that and the candidate's agreement, then multiply by the importance.  Sum the total.
		var agreement = 0f
		issueImportance.keys.forEach({ issue ->
			agreement += (issueAgreement[issue]!!*candidate.issueAgreement.getOrDefault(issue, 0f))*issueImportance[issue]!!
		})
		return agreement/maxAgreement
	}
}
