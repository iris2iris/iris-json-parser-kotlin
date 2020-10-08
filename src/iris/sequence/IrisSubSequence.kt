package iris.sequence

/**
 * @created 01.08.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisSubSequence(private val source: CharSequence, val start: Int, val end: Int) : IrisSequence {

	private val len = end - start
	override val length: Int
		get() = len

	override fun get(index: Int): Char {
		return source[start + index]
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		return IrisSubSequence(source, this.start + startIndex, this.start + endIndex)
	}

	override fun toString(): String {
		return source.subSequence(start, end).toString()
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append(source, start, end)
		return buffer
	}

	private var hash = 0
	private var hashed = false
	override fun hashCode(): Int {
		if (hashed)
			return hash
		var res = 0
		for (i in 0 until length)
			res = (res * 33) + source[start + i].toInt()
		hash = res
		hashed = true
		return res
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CharSequence) return false
		if (length != other.length) return false
		for (i in 0 until length)
			if (source[start + i] != other[i])
				return false
		return true
	}
}