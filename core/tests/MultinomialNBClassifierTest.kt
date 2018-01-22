
import com.josephcatrambone.debatesimulator.Demographic
import org.junit.Test
import org.junit.Assert.*
import com.josephcatrambone.debatesimulator.nlp.MultinomialNBClassifier
import com.josephcatrambone.debatesimulator.nlp.Tokenizer
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MultinomialNBClassifierTest {

	val indexToWord = arrayOf<String>()

	fun saveDemographic(dem:Demographic, fname:String) {
		val objOut = ObjectOutputStream(File(fname).outputStream())
		objOut.writeObject(dem)
		objOut.close()
	}

	@Test
	fun testNBClassify() {
		val classifier = MultinomialNBClassifier(indexToWord.size, 2)

	}

	@Test
	fun testSaveNBClassifier() {
		val classifier = MultinomialNBClassifier(4, 2)
		classifier.fit(arrayOf(
			floatArrayOf(1f, 1f, 0f, 0f),
			floatArrayOf(1f, 0f, 1f, 0f),
			floatArrayOf(0f, 1f, 0f, 1f),
			floatArrayOf(0f, 0f, 1f, 1f)
		), intArrayOf(
				0,
				0,
				1,
				1
		))

		assertEquals(0, classifier.predict(floatArrayOf(1f, 0f, 0f, 0f)))

		// Save
		val obout = ObjectOutputStream(File("classifier_test.out").outputStream())
		obout.writeObject(classifier)
		obout.close()

		// Restore
		val obin = ObjectInputStream(File("classifier_test.out").inputStream())
		val cls = obin.readObject() as MultinomialNBClassifier
		obin.close()

		assertEquals(0, cls.predict(floatArrayOf(1f, 0f, 0f, 0f)))
	}

	@Test
	fun buildLiterallyDogsDemographic() {
		val dislikeStatements = arrayOf(
			"Bath",
			"Vet"
		)

		val likeStatements = arrayOf(
			"Food",
			"Walk",
			"Good boy",
			"Good boys",
			"Dinner",
			"Ball",
			"Fetch",
			"Park"
		)

		val examples = dislikeStatements.plus(likeStatements)
		val labels = IntArray(examples.size, { i -> if(i < dislikeStatements.size) { 0 } else { 1 }})

		val tokenizer = Tokenizer(examples)
		val sentiment = MultinomialNBClassifier(tokenizer.numTokens, 2)
		sentiment.fit(tokenizer.run(examples), labels)

		val dem = Demographic(
				0.5f,
				tokenizer,
				sentiment,
				"Dogs",
				"The literally dogs demographic is literally dogs.  They'll probably love you unconditionally.  Dogs are so good."
		)

		saveDemographic(dem, "dogs.demographic")
	}

	@Test
	fun buildAntiVaxxerDemographic() {
		val dislikeStatements = arrayOf(
			"We need to trust and understand the scientific consensus.",
			"We must follow the guidance of evidence based medicine.",
			"It is abhorrent that we should subject our children to preventable diseases.",
			"I believe in the importance of vaccination.",
			"Each of us has a social obligation to support herd immunity.",
			"Every child needs to be vaccinated.",
			"Herd immunity is an important aspect of modern society.  We have a social obligation to maintain it."
		)

		val likeStatements = arrayOf(
			"Vaccines are unsafe.",
			"They're not proven safe.",
			"Vaccines will give my child autism.",
			"It's my right as a parent to choose what's best for my child.",
			"I know what's best for my kids."
		)

		val examples = dislikeStatements.plus(likeStatements)
		val labels = IntArray(examples.size, { i -> if(i < dislikeStatements.size) { 0 } else { 1 }})

		val tokenizer = Tokenizer(examples)
		val sentiment = MultinomialNBClassifier(tokenizer.numTokens, 2)
		sentiment.fit(tokenizer.run(examples), labels)

		val dem = Demographic(
			0.5f,
			tokenizer,
			sentiment,
			"Anti-vaxxers",
			"Anti-vaxxers are people who believe it's their right as parents to decide what exterminated diseases come roaring back into the population."
		)

		saveDemographic(dem, "antivax.demographic")
	}

	@Test
	fun buildConservativeDemographic() {
		val dislikeStatements = arrayOf(
			"I support gay marriage.",
			"I support same sex marriage.",
			"Homosexuality is not a choice.",
			"From each according to his ability, to each according to his need.",
			"It is possible to be good without god.",
			"We must emphasize the importance of the separation of church and state.",
			"Corporations should not prey on the poor."
		)

		val likeStatements = arrayOf(
			"Poor people are lazy.",
			"I believe in God.",
			"Guns, Jesus, and America.",
			"Foreigners and Mexicans are taking our jobs."
		)

		val examples = dislikeStatements.plus(likeStatements)
		val labels = IntArray(examples.size, { i -> if(i < dislikeStatements.size) { 0 } else { 1 }})

		val tokenizer = Tokenizer(examples)
		val sentiment = MultinomialNBClassifier(tokenizer.numTokens, 2)
		sentiment.fit(tokenizer.run(examples), labels)

		val dem = Demographic(
				0.5f,
				tokenizer,
				sentiment,
				"Conservatives",
				"Conservatives believe in traditional values and favor individual liberties over collective abilities."
		)

		saveDemographic(dem, "conservative.demographic")
	}

	@Test
	fun buildHipsterDemographic() {
		val dislikeStatements = arrayOf(
			"Starbucks",
			"Peet's Coffee",
			"Fucking Hipsters",
			"Bullshit plaid",
			"Clean shaven good looks.",
			"Well groomed and well bathed"
		)

		val likeStatements = arrayOf(
			"Small batch",
			"Artisanal coffee",
			"Kale salad",
			"You've probably never heard of it.",
			"Artisan Coffee",
			"Vinal",
			"Vinyl",
			"Record collection",
			"Acoustic",
			"Microbatch",
			"Microbrewery"
		)

		val examples = dislikeStatements.plus(likeStatements)
		val labels = IntArray(examples.size, { i -> if(i < dislikeStatements.size) { 0 } else { 1 }})

		val tokenizer = Tokenizer(examples)
		val sentiment = MultinomialNBClassifier(tokenizer.numTokens, 2)
		sentiment.fit(tokenizer.run(examples), labels)

		val dem = Demographic(
				0.2f,
				tokenizer,
				sentiment,
				"Hipsters",
				"Not quite hippies, but fond of wool and beards and weird coffee and shit you haven't heard of."
		)

		saveDemographic(dem, "hipsters.demographic")
	}

	@Test
	fun buildGunNutsDemographic() {
		val dislikeStatements = arrayOf(
				"Guns are stupid.",
				"Thousands of innocent people are killed every year by guns.",
				"Thousands of guilty people are killed every year by guns in ways that are not cool.",
				"Gun violence is the leading cause of deaths in the developed world.",
				"Bans significantly reduce gun violence.",
				"Gun buyback programs have been shown effective in other countries.",
				"We can't let more innocent children be killed by school shootings.",
				"I support gun control.",
				"Guns are directly linked to thousands of preventable deaths each year.",
				"The second amendment is antiquated.  It was meant for an older time with less powerful weapons.",
				"The 2nd amendment is no longer relevant in today's society."
		)

		val likeStatements = arrayOf(
				"I am pro second amendment.",
				"I am pro-second amendment.",
				"I'm a big fan of the 2nd amendment.",
				"I'm a supporter of 2nd amendment rights.",
				"I support a person's right to bear arms.",
				"Guns kick ass.",
				"Kill 'em all",
				"Guns are great.",
				"I love guns.",
				"I bought a gun for my guns.",
				"I believe in our second amendment rights.",
				"Gun control is for wusses.",
				"That grandma needed a gun.",
				"Toddlers should be better trained in the use of automatic weapons.",
				"Safety and supervision are paramount if you're a coward.",
				"Personal freedoms count more than the lives of other people.",
				"I have several handguns myself.",
				"My favorite part of the gun is the gun part of the gun."
		)

		val examples = dislikeStatements.plus(likeStatements)
		val labels = IntArray(examples.size, { i -> if(i < dislikeStatements.size) { 0 } else { 1 }})

		val tokenizer = Tokenizer(examples)
		val sentiment = MultinomialNBClassifier(tokenizer.numTokens, 2)
		sentiment.fit(tokenizer.run(examples), labels)

		val gunNuts = Demographic(
			0.7f,
			tokenizer,
			sentiment,
			"Gun Nuts",
			"They really love guns.  Try not to say 'buyback' or 'violence' or 'shootings'."
		)

		val dSentiment = gunNuts.updateSentiment("Guns are for morons.")

		saveDemographic(gunNuts, "gunnuts.demographic")
	}

	@Test
	fun buildLiberalDemographic() {
		// Left = proponent of govt regulation of business.
		val likeStatements = arrayOf(
			"The free market alone should not and cannot act in the best interests of the people.",
			"Regulation and government oversight are essential for the betterment of all human kind.",
			"A free market is not a fair market.",
			"We must strengthen consumer protections.",
			"We must strive toward a fair wage -- a living wage.",
			"The rich get richer.  The poor get poorer.  We subsidize the super-wealthy on the backs of the working class.",
			"No country has ever occupied a higher plane of material well-being than ours at the present moment. This well-being is due to no sudden or accidental causes, but to the play of the economic forces in this country for over a century; to our laws, our sustained and continuous policies; above all, to the high individual average of our citizenship. Great fortunes have been won by those who have taken the lead in this phenomenal industrial development, and most of these fortunes have been won not by doing evil, but as an incident to action which has benefited the community as a whole. Never before has material well-being been so widely diffused among our people. Great fortunes have been accumulated, and yet in the aggregate these fortunes are small Indeed when compared to the wealth of the people as a whole. The plain people are better off than they have ever been before. The insurance companies, which are practically mutual benefit societies--especially helpful to men of moderate means--represent accumulations of capital which are among the largest in this country. There are more deposits in the savings banks, more owners of farms, more well-paid wage-workers in this country now than ever before in our history. Of course, when the conditions have favored the growth of so much that was good, they have also favored somewhat the growth of what was evil. It is eminently necessary that we should endeavor to cut out this evil, but let us keep a due sense of proportion; let us not in fixing our gaze upon the lesser evil forget the greater good. The evils are real and some of them are menacing, but they are the outgrowth, not of misery or decadence, but of prosperity--of the progress of our gigantic industrial development. This industrial development must not be checked, but side by side with it should go such progressive regulation as will diminish the evils. We should fail in our duty if we did not try to remedy the evils, but we shall succeed only if we proceed patiently, with practical common sense as well as resolution, separating the good from the bad and holding on to the former while endeavoring to get rid of the latter.",
			"I discussed at length the question of the regulation of those big corporations commonly doing an interstate business, often with some tendency to monopoly, which are popularly known as trusts. The experience of the past year has emphasized, in my opinion, the desirability of the steps I then proposed. A fundamental requisite of social efficiency is a high standard of individual energy and excellence; but this is in no wise inconsistent with power to act in combination for aims which can not so well be achieved by the individual acting alone. ",
			"A fundamental base of civilization is the inviolability of property; but this is in no wise inconsistent with the right of society to regulate the exercise of the artificial powers which it confers upon the owners of property, under the name of corporate franchises, in such a way as to prevent the misuse of these powers.",
			"Corporations, and especially combinations of corporations, should be managed under public regulation.",
			"Our aim is not to do away with corporations; on the contrary, these big aggregations are an inevitable development of modern industrialism, and the effort to destroy them would be futile unless accomplished in ways that would work the utmost mischief to the entire body politic.",
			"We can do nothing of good in the way of regulating and supervising these corporations until we fix clearly in our minds that we are not attacking the corporations, but endeavoring to do away with any evil in them.",
			"We are not hostile to them; we are merely determined that they shall be so handled as to subserve the public good.",
			"We draw the line against misconduct, not against wealth. The capitalist who, alone or in conjunction with his fellows, performs some great industrial feat by which he wins money is a welldoer, not a wrongdoer, provided only he works in proper and legitimate lines.",
			"In curbing and regulating the combinations of capital which are, or may become, injurious to the public we must be careful not to stop the great enterprises which have legitimately reduced the cost of production, not to abandon the place which our country has won in the leadership of the international industrial world, not to strike down wealth with the result of closing factories and mines, of turning the wage-worker idle in the streets and leaving the farmer without a market for what he grows.",
			"No more important subject can come before the Congress than this of the regulation of interstate business.",
			"""I believe that monopolies, unjust discriminations, which prevent or cripple competition, fraudulent overcapitalization, and other evils in trust organizations and practices which injuriously affect interstate trade can be prevented under the power of the Congress to "regulate commerce with foreign nations and among the several States" through regulations and requirements operating directly upon such commerce, the instrumentalities thereof, and those engaged therein."""
		)
		val dislikeStatements = arrayOf<String>(
			"The administration took this action with great regret, because it's clear that the massive deficits our government runs is one of the root causes of our profound economic problems, and for too many years this process has come too easily for us.",
			"We've lived beyond our means and then financed our extravagance on the backs of the American people.",
			"The clear message I received in the election campaign is that we must gain control of this inflationary monster.",
			"Within moments of taking the oath of office, I placed a freeze on the hiring of civilian employees in the Federal Government.",
			"Two days later I issued an order to cut down on government travel, reduce the number of consultants to the government, stopped the procurement of certain items, and called on my appointees to exercise restraint in their own offices.",
			"Yesterday I announced the elimination of remaining Federal controls on U. S. oil production and marketing.",
			"Today I'm announcing two more actions to reduce the size of the Federal Government.",
			"First, I'm taking major steps toward the elimination of the Council on Wage and Price Stability. This Council has been a failure.",
			"It has been totally ineffective in controlling inflation, and it's imposed unnecessary burdens on labor and business. Therefore, I am now ending the wage and price program of the Council.",
			"I am eliminating the staff that carries out its wage/pricing activities, and I'm asking Congress to rescind its budget, saving the taxpayers some million a year.",
			"My second decision today is a directive ordering key Federal agencies to freeze pending regulations for 60 days.",
			"This action gives my administration time to start a new regulatory oversight process and also prevents certain last-minute regulatory decisions of the previous administration, the so-called midnight regulations, from taking effect without proper review and approval.",
			"All of us should remember that the Federal Government is not some mysterious institution comprised of buildings, files, and paper. The people are the government. What we create we ought to be able to control."
		)

		val examples = dislikeStatements.plus(likeStatements)
		val labels = IntArray(examples.size, { i -> if(i < dislikeStatements.size) { 0 } else { 1 }})

		val tokenizer = Tokenizer(examples)
		val sentiment = MultinomialNBClassifier(tokenizer.numTokens, 2)
		sentiment.fit(tokenizer.run(examples), labels)

		val liberalDemographic = Demographic(
				0.7f,
				tokenizer,
				sentiment,
				"Liberals",
				"Favor a larger government.  They believe the purpose of government is to make a level playing field, to protect consumers, and to provide a social safety net."
		)

		val dSentiment = liberalDemographic.updateSentiment("Violence is bad.")

		saveDemographic(liberalDemographic, "liberal.demographic")
	}



}
