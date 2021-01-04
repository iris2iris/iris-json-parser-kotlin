package iris.json.flow

import iris.json.Util.ValueType

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 *
 */
abstract class TokenerAbstractWithPointer : Tokener {

	override fun nextChar(): Char {
		skipWhitespaces()
		return curCharInc() ?: throw exception("End of source data")
	}

	protected abstract fun curChar(): Char?
	protected abstract fun curCharInc(): Char?
	protected abstract fun moveNext()
	protected abstract fun pointer(): Int
	protected abstract fun charSequence(start: Int, end: Int): CharSequence

	private fun skipWhitespaces() {
		do {
			val char = curChar() ?: break
			if (!char.isWhitespace()) {
				break
			}
			moveNext()
		} while (true)
	}

	override fun readString(quote: Char): CharSequence {
		var escaping = false
		val start = this.pointer()
		do {
			val char = curCharInc() ?: break
			if (char == '\\')
				escaping = true
			else if (escaping) {
				escaping = false
			} else if (char == quote) {
				break
			}
		} while (true)
		return charSequence(start, pointer() - 1)
	}

	override fun readPrimitive(): Tokener.PrimitiveData {
		var curType = ValueType.Integer
		val first = pointer()
		var isFirst = true
		loop@ do {
			val char = curChar() ?: break
			when {
				char.isDigit() -> {
				}
				char == '-' -> if (!isFirst) curType = ValueType.Constant
				char == '.' -> if (curType == ValueType.Integer) curType = ValueType.Float
				char.isLetter() -> curType = ValueType.Constant
				else -> break@loop
			}
			if (isFirst)
				isFirst = false
			moveNext()
		} while (true)
		return Tokener.PrimitiveData(charSequence(first, pointer()), curType)
	}

	override fun readFieldName(quote: Char?): CharSequence {
		return if (quote==null) readPrimitive().sequence else readString(quote)
	}

}