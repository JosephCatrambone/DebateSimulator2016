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

data class Standing(val left:String, val right:String) // AKA Theme
data class Sentiment(val summary:String, val direction:Float, val topics:Set<Topic>, val standing:Standing)
// Example: summary = Should the government increase environmental regulations to prevent climate change?

class Demographic(
	val populationSize:Int = 0,
	val baseVotingLikelihood:Float = 0.5f, // The odds that a member of this group will case a vote.
	val issueImportance:Map<Sentiment, Float> = mapOf<Sentiment, Float>(), // Sentiment -> 0, 1, // 0 - not important.  1 = super important.
	val issueAgreement:Map<Sentiment, Float> = mapOf<Sentiment, Float>() // Sentiment -> [-1, 1] // Disagree, agree.
) {

}
