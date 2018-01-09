package com.josephcatrambone.debatesimulator

enum class Topics {
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

data class Issue(val summary:String, val topics:Set<Topics>)

class CandidateModel() {
	private val statements = mutableListOf<String>()
	val issueImportance = mapOf<Issue, Float>() // 0 = candidate doesn't talk about issue.
	val issueAgreement = mapOf<Issue, Float>() // -1 = candidate does not agree with sentiment.  1 = candidate agrees with sentiment.
	var issuesNeedUpdate = true // If the issues haven't been rebuilt after adding a statement, this is true.

	fun addStatement(str: String) {
		issuesNeedUpdate = true
		statements.add(str)
	}
}

class Demographic(
	val populationSize:Int = 0,
	val baseVotingLikelihood:Float = 0.5f, // The odds that a member of this group will case a vote.
	val issueImportance:Map<Issue, Float> = mapOf<Issue, Float>(), // Issue -> 0, 1, // 0 - not important.  1 = super important.
	val issueAgreement:Map<Issue, Float> = mapOf<Issue, Float>() // Issue -> [-1, 1] // Disagree, agree.
) {
	fun getFondness(candidate:CandidateModel): Float {
		// Basic dot product.
		// If the candidate HASN'T spoken about a very important issue, this will decrease their fondness, but only for one issue.
		// We do this by selecting the most important issue for this group.
		var maxAgreement = 0f // While we're iterating, also sum the max agreement possible.
		//var (wedgeIssue, wedgeValue) = issueImportance.entries.first()
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
