package iris.json.flow

import iris.json.IrisJson

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
abstract class TokenerAbstractWithSequence() : Tokener {

	override fun nextChar(): Char {
		skipWhitespaces()
		return curCharInc() ?: throw exception("End of source data")
	}

	protected abstract fun curChar(): Char?
	protected abstract fun curCharInc(): Char?
	protected abstract fun moveNext()
	protected abstract fun sequenceStart(): TokenSequence

	interface TokenSequence {
		fun finish(shift: Int = 0): CharSequence
		fun append(char: Char)
	}

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
		val seq = this.sequenceStart()
		do {
			val char = curCharInc()?: break
			if (char == '\\')
				escaping = true
			else if (escaping) {
				escaping = false
			} else if (char == quote) {
				break
			}
			seq.append(char)
		} while (true)
		return seq.finish(-1)
	}

	override fun readPrimitive(): Tokener.PrimitiveData {
		var curType = IrisJson.ValueType.Integer
		val seq = this.sequenceStart()
		var isFirst = true
		loop@ do {
			val char = curChar() ?: break
			when {
				char.isDigit() -> {}
				char == '-' -> if (!isFirst) curType = IrisJson.ValueType.Constant
				char == '.' -> if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				char.isLetter() -> curType = IrisJson.ValueType.Constant
				else -> break@loop
			}
			if (isFirst)
				isFirst = false
			moveNext()
			seq.append(char)
		} while (true)
		return Tokener.PrimitiveData(seq.finish(), curType)
	}

	override fun readFieldName(quote: Char?): CharSequence {
		return if (quote==null) readPrimitive().sequence else readString(quote)
	}
}