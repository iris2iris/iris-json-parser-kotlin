package iris.sequence

import kotlin.math.min

abstract class CharArraySourceAbstract : CharArraySource {

	override fun toCharArray(): CharArray {
		val len = length
		return toCharArray(CharArray(len), 0, 0, len)
	}

	override fun toCharArray(start: Int, len: Int): CharArray {
		val len = min(length, len)
		return toCharArray(CharArray(len), 0, start, len)
	}

	override fun toCharArray(dest: CharArray): CharArray {
		return toCharArray(dest, 0, 0, length)
	}
}