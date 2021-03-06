package iris.json.flow

import iris.json.Util.ValueType
import iris.sequence.IrisSequenceCharArray
import kotlin.math.max
import kotlin.math.min

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class TokenerString(source: String) : Tokener {

	companion object {
		const val SPACE = ' '.toInt()
		const val TAB = '\t'.toInt()
		const val LF = '\n'.toInt()
		const val CR = '\r'.toInt()
	}

	val source = source.toCharArray()
	var pointer: Int = 0

	override fun nextChar(): Char {
		skipWhitespaces()
		return source[pointer++]
	}

	private fun skipWhitespaces() {
		val len = source.size
		do {
			val char = source[pointer].toInt()
			if (char > SPACE)
				break
			if (!(char == SPACE || char == TAB || char == LF || char == CR))
				break
			pointer++
		} while (pointer < len)
	}

	override fun getSourceSequence(start: Int, end: Int): CharSequence {
		return IrisSequenceCharArray(source, start, if (end == 0) source.size else end)
	}

	override fun exception(s: String): IllegalArgumentException {
		return IllegalArgumentException(s + "\nPosition $pointer: " + getPlace())
	}

	private fun getPlace(): String {
		val start = max(0, pointer - 20)
		val end = min(pointer + 20, source.size - 1)
		return '"' + String(source.copyOfRange(start, end)) + '"'
	}

	override fun readString(quote: Char): CharSequence {
		var escaping = false
		val len = source.size
		val start = this.pointer
		do {
			val char = source[pointer++]
			if (escaping) {
				escaping = false
			} else if (char == '\\')
				escaping = true
			else if (char == quote) {
				break
			}
		} while (pointer < len)
		return IrisSequenceCharArray(source, start, pointer - 1)
	}

	override fun readFieldName(quote: Char?): CharSequence {
		return if (quote == null) readQuotelessFieldName() else readString(quote)
	}

	private fun readQuotelessFieldName(): CharSequence {
		val first = pointer
		val len = source.size
		do {
			if (!(source[pointer] in '0'..'9' || source[pointer] in 'a'..'z' || source[pointer] in 'A'..'Z'))
				break
			pointer++
		} while (pointer < len)
		return IrisSequenceCharArray(source, first, pointer)
	}

	override fun readPrimitive(): Tokener.PrimitiveData {
		var curType = ValueType.Integer
		val first = pointer
		val len = source.size
		loop@ do {
			when (source[pointer]) {
				in '0'..'9' -> {}
				'-' -> if (first != pointer) curType = ValueType.Constant
				'.' -> if (curType == ValueType.Integer) curType = ValueType.Float
				in 'a'..'z', in 'A'..'Z' -> curType = ValueType.Constant
				else -> break@loop
			}
			pointer++
		} while (pointer < len)
		return Tokener.PrimitiveData(IrisSequenceCharArray(source, first, pointer), curType)
	}

	override fun back() {
		pointer--
	}

}