package iris.json

import java.lang.Appendable
import java.lang.StringBuilder

class IrisSequence(private val source: CharSequence, private val start: Int, private val end: Int) : CharSequence {
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

	fun <A: Appendable> joinTo(buffer: A): A {
		if (buffer is StringBuilder)
			buffer.append(source, start, end)
		else
			buffer.append(this.toString())
		return buffer
	}
}