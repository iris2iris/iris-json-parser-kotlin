package iris.json.flow

import iris.sequence.CharArrayBuilder
import java.io.File
import java.io.Reader
import java.io.StringReader

/**
 * TODO: Works very slow. Need to get, how does [org.json.JSONTokener] works faster with same idea
 *
 * Use [iris.json.flow.TokenerBufferedReader] instead
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 * */
class TokenerSimpleReader(private val reader: Reader): TokenerAbstractWithSequence() {

	private var isEof = false
	private var curChar: Char? = null
	private var usePrev = false
	private var prevChar: Char? = null

	override fun curChar(): Char? {
		if (usePrev)
			return prevChar
		if (isEof) return null

		if (curChar != null) return curChar
		val value = reader.read()
		if (value == -1) {
			isEof = true
			usePrev = false
			curChar = null
			return null
		}
		curChar = value.toChar()
		return curChar
	}

	override fun curCharInc(): Char? {
		val ch = curChar()?: return null
		if (usePrev) {
			usePrev = false
		} else {
			prevChar = curChar
			curChar = null
		}
		return ch
	}

	override fun moveNext() {
		curCharInc()
	}

	override fun exception(s: String): IllegalArgumentException {
		TODO(s)
	}

	override fun back() {
		usePrev = true
	}

	override fun sequenceStart(): TokenSequence {
		return TImpl()
	}

	fun close() {
		reader.close()
	}

	private class TImpl : TokenSequence {

		private val buff = CharArrayBuilder(16)

		override fun finish(shift: Int): CharSequence {
			return buff
		}

		override fun append(char: Char) {
			buff.append(char)
		}
	}
}