package iris.sequence

/**
 * @created 01.08.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisSequenceSingleChar(private val ch: Char) : IrisSequence {
	override val length: Int
		get() = 1

	override fun get(index: Int): Char {
		if (index != 0) throw IndexOutOfBoundsException("$index is not 0 for single char sequence")
		return ch
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		if (startIndex != 0 && endIndex != 1)
			throw IndexOutOfBoundsException("Single char sequence can only be [0 to 1]")
		return this
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append(ch)
		return buffer
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as IrisSequenceSingleChar
		if (ch != other.ch) return false
		return true
	}

	override fun hashCode(): Int {
		return ch.hashCode()
	}


}