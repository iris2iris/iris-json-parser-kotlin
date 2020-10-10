package iris.json.flow

import iris.json.IrisJson
import iris.sequence.IrisSequenceCharArray

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

	private val source = source.toCharArray()
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

	override fun exception(s: String): IllegalArgumentException {
		return IllegalArgumentException(s + " in position $pointer\n" + getPlace())
	}

	private fun getPlace(): String {
		return ""
		//return '"' + source.substring(max(0, pointer - 10), min(pointer + 10, source.length - 1)) + '"'
	}

	override fun readString(quote: Char): CharSequence {
		var escaping = false
		val len = source.size
		val start = this.pointer
		do {
			val char = source[pointer++]
			if (char == '\\')
				escaping = true
			else if (escaping) {
				escaping = false
			} else if (char == quote) {
				break
			}
		} while (pointer < len)
		return IrisSequenceCharArray(source, start, pointer - 1)
	}

	override fun readFieldName(quote: Char?): CharSequence {
		//val seq = readString(quote)
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
		var curType = IrisJson.ValueType.Integer
		val first = pointer
		val len = source.size
		loop@ do {
			/*val char = source[pointer]
			when {
				char.isDigit() -> {
				}
				char == '-' -> if (first != pointer) curType = IrisJson.ValueType.Constant
				char == '.' -> if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				char.isLetter() -> curType = IrisJson.ValueType.Constant
				else -> break@loop
			}*/
			when (source[pointer]) {
				in '0'..'9' -> {}
				'-' -> if (first != pointer) curType = IrisJson.ValueType.Constant
				'.' -> if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				in 'a'..'z', in 'A'..'Z' -> curType = IrisJson.ValueType.Constant
				else -> break@loop
			}
			pointer++
		} while (pointer < len)
		return Tokener.PrimitiveData(IrisSequenceCharArray(source, first, pointer), curType)
		//return Tokener.PrimitiveData(stringCache.convertToString(IrisSequenceCharArray(source, first, pointer)), curType)
	}

	override fun back() {
		pointer--
	}

}