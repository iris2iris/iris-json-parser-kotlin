package iris.json.flow

import iris.json.IrisJsonItem
import iris.json.IrisJsonNull
import iris.sequence.IrisSequence
import java.lang.Appendable

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowString(tokener: Tokener, val quote: Char) : FlowItem(tokener) {

	private val data: CharSequence by lazy(LazyThreadSafetyMode.NONE) { this.tokener.readString(quote) }

	override fun toString(): String {
		return '"' + data.toString() + '"'
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append('"')
		(data as? IrisSequence)?.joinTo(buffer) ?: buffer.append(data)
		buffer.append('"')
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun parse() {
		data
	}

	private val ready by lazy(LazyThreadSafetyMode.NONE) { init() }

	private fun init(): String {
		val res = StringBuilder()
		val len = data.length
		if (len == 0)
			return ""
		var isEscape = false
		var fromIndex = 0
		var i = 0
		do {
			val ch = data[i]
			if (isEscape) {
				isEscape = false
				val repl = when (ch) {
					'"' -> '"'
					'n' -> '\n'
					'b' -> '\b'
					'/' -> '/'
					'r' -> '\r'
					't' -> '\t'
					'u' -> 'u'
					else -> '-'
				}
				if (ch != '-') {
					res.append(data, fromIndex, i - 1)
					if (repl == 'u') {
						val d = data.subSequence(i + 1, i + 1 + 4).toString().toInt(16)
						res.appendCodePoint(d)
						i += 4
					} else {
						res.append(repl)
					}
					fromIndex = i + 1
				}
			} else {
				if (ch == '\\')
					isEscape = true
			}
			i++
		} while (i < len)

		return if (fromIndex == 0) // no any escape
			data.toString()
		else {
			if (fromIndex != len) {
				res.append(data, fromIndex, len)
			}
			res.toString()
		}
	}

	override fun obj(): Any? {
		return ready
	}
}
