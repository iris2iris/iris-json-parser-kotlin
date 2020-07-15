package iris.json

class IrisSequence(private val source: String, private val start: Int, private val end: Int) : CharSequence {
	override val length: Int
		get() = end - start

	override fun get(index: Int): Char {
		return source[start + index]
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		return IrisSequence(source, start + startIndex, start + endIndex)
	}

	override fun toString(): String {
		return source.substring(start, end)
	}
}