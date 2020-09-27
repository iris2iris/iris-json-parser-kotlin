package iris.json.flow

import iris.json.IrisJson
import iris.sequence.IrisSubSequence
import kotlin.math.max
import kotlin.math.min

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class TokenerString(val source: String) : Tokener {

	var pointer: Int = 0

	override fun nextChar(): Char {
		skipWhitespaces()
		return source[pointer++]
	}

	private fun skipWhitespaces() {
		val len = source.length
		do {
			val char = source[pointer]
			if (!char.isWhitespace()) {
				break
			}
			pointer++
		} while (pointer < len)
	}

	override fun exception(s: String): IllegalArgumentException {
		return IllegalArgumentException(s + " in position $pointer\n" + getPlace())
	}

	private fun getPlace(): String {
		return '"' + source.substring(max(0, pointer - 10), min(pointer + 10, source.length - 1)) + '"'
	}

	override fun readString(quote: Char): CharSequence {
		var escaping = false
		val len = source.length
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
		return IrisSubSequence(source, start, pointer - 1)
	}

	override fun readPrimitive(): Tokener.PrimitiveData {
		var curType = IrisJson.ValueType.Integer
		val first = pointer
		val len = source.length
		loop@ do {
			val char = source[pointer]
			when {
				char.isDigit() -> {
				}
				char == '-' -> if (first != pointer) curType = IrisJson.ValueType.Constant
				char == '.' -> if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				char.isLetter() -> curType = IrisJson.ValueType.Constant
				else -> break@loop
			}
			pointer++
		} while (pointer < len)
		return Tokener.PrimitiveData(IrisSubSequence(source, first, pointer), curType)
	}

	override fun back() {
		pointer--
	}

}