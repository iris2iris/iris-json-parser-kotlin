package iris.json.flow

import iris.sequence.CharArrayBuilder
import iris.sequence.IrisSubSequence
import java.io.Reader
import kotlin.math.max
import kotlin.math.min

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class TokenerContentBuildReader(private val reader: Reader, private val buffer: CharArray = CharArray(DEFAULT_BUFFER_SIZE)): TokenerAbstractWithPointer() {

	companion object {
		const val DEFAULT_BUFFER_SIZE = 4*1024
	}
	private var pointer = 0
	private val content = CharArrayBuilder(buffer.size)

	override fun curChar(): Char? {
		if (content.length <= pointer) {
			val am = reader.read(buffer)
			if (am == -1)
				return null
			content.append(buffer, 0, am)
		}
		return content[pointer]
	}

	override fun curCharInc(): Char? {
		val ch = curChar()
		pointer++
		return ch
	}

	override fun moveNext() {
		pointer++
	}

	override fun pointer(): Int {
		return pointer
	}

	override fun charSequence(start: Int, end: Int): CharSequence {
		return IrisSubSequence(content, start, end)
	}

	override fun exception(s: String): IllegalArgumentException {
		return IllegalArgumentException(s + " in position $pointer\n" + getPlace())
	}

	private fun getPlace(): String {
		return '"' + content.substring(max(0, pointer - 10), min(pointer + 10, content.length - 1))+'"'
	}

	override fun back() {
		pointer--
	}
}