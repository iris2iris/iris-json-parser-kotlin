package iris.sequence

/**
 * @created 25.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface CharArraySource {

	val length: Int
	operator fun get(index: Int): Char

	fun toCharArray(): CharArray
	fun toCharArray(start: Int = 0, len: Int = length): CharArray

	fun toCharArray(dest: CharArray): CharArray
	fun toCharArray(dest: CharArray, destOffset: Int = 0, start: Int = 0, len: Int = dest.size): CharArray
}

