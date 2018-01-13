import com.josephcatrambone.debatesimulator.nlp.Tokenizer
import org.junit.Test
import org.junit.Assert.*

class TokenizerTest {
	@Test
	fun checkSplit() {
		val s = "This should get broken up."
		val tok = Tokenizer(arrayOf(s))
		val words = tok.split(s)
		val tokens = tok.tokenize(words)
	}
}