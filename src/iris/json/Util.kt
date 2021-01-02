package iris.json

/**
 * @created 02.01.2021
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
object Util {

	private val digitRange = '0'..'9'
	private val alphaRange = 'a'..'z'
	private val alphaUpperRange = 'A'..'Z'

	fun isDigit(char: Char): Boolean {
		return digitRange.contains(char)
	}

	fun isAlpha(char: Char): Boolean {
		return alphaRange.contains(char) || alphaUpperRange.contains(char)
	}

	fun isDigitOrAlpha(char: Char): Boolean {
		return isDigit(char) || isAlpha(char)
	}
}