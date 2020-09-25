package iris.sequence

/**
 * @created 01.08.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisSequenceCharArray(val source: CharArray, val start: Int = 0, val end: Int = source.size) : IrisSequence, CharArraySource {

	override val length: Int
		get() = end - start

	override fun get(index: Int): Char {
		return source[start + index]
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		return IrisSequenceCharArray(source,this.start + startIndex,this.start + endIndex)
	}

	override fun toString(): String {
		return String(source, start, length)
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		when (buffer) {
			is StringBuilder -> buffer.append(source, start, length)
			is StringBuffer -> buffer.append(source, start, length)
			else -> buffer.append(this/*.toString()*/)
		}
		return buffer
	}

	override fun hashCode(): Int {
		val l = length
		return l + if (l == 0) 0 else get(0).toInt()*31
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

	override fun toCharArray(): CharArray {
		val len = length
		return toCharArray(CharArray(len), 0, 0, len)
	}

	override fun toCharArray(start: Int, len: Int): CharArray {
		return toCharArray(CharArray(len), 0, start, len)
	}

	override fun toCharArray(dest: CharArray): CharArray {
		return toCharArray(dest, 0, start, end - start)
	}

	override fun toCharArray(dest: CharArray, destOffset: Int, start: Int, len: Int): CharArray {
		val st = this.start + start
		source.copyInto(dest, destOffset, st, st + len)
		//System.arraycopy(source, this.start + start, dest, destOffset, len)
		return dest
	}
}