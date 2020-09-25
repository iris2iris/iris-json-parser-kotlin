package iris.sequence

import kotlin.math.min

/**
 * @created 01.08.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisSequenceCharList(source: List<Char>, start: Int = 0, end: Int = source.size) : IrisSequence, CharArraySource {

	private val subList = if (start == 0 && end == source.size) source else source.subList(start, end)

	override val length: Int
		get() = subList.size

	override fun get(index: Int): Char {
		return subList[index]
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		return IrisSequenceCharList(subList, startIndex, endIndex)
	}

	override fun toString(): String {
		return String(charArray)
	}

	private val charArray by lazy(LazyThreadSafetyMode.NONE) { subList.toCharArray() }

	override fun <A : Appendable> joinTo(buffer: A): A {
		when (buffer) {
			is StringBuilder -> buffer.append(charArray)
			is StringBuffer -> buffer.append(charArray)
			else -> buffer.append(/*IrisSequenceCharArray(charArray)*/this.toString())
		}
		return buffer
	}

	override fun hashCode(): Int {
		val l = subList.size
		return l + if (l == 0) 0 else get(0).toInt()*1023
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CharSequence) return false
		if (length != other.length) return false
		for (i in 0 until length)
			if (subList[i] != other[i])
				return false
		return true
	}

	override fun toCharArray(): CharArray {
		val len = length
		return toCharArray(CharArray(len), 0, 0, len)
	}

	override fun toCharArray(start: Int, len: Int): CharArray {
		return toCharArray(CharArray(len), 0, start, len)
	}

	override fun toCharArray(dest: CharArray): CharArray {
		return toCharArray(dest, 0, 0, min(dest.size, subList.size))
	}

	override fun toCharArray(dest: CharArray, destOffset: Int, start: Int, len: Int): CharArray {
		charArray.copyInto(dest, destOffset, start, start + len)
		//System.arraycopy(charArray, start, dest, destOffset, len)
		return dest
	}
}