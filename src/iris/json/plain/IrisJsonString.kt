package iris.json.plain

import iris.json.JsonString
import iris.sequence.toInt

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonString(private val data: CharSequence/*, private val escapes: ArrayList<Int>?*/) : IrisJsonItem(), JsonString {

	override fun <A : Appendable> appendToJsonString(buffer: A): A {
		buffer.append('"')
		buffer.append(data)
		//data.joinTo(buffer)
		buffer.append('"')
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	private var ready: String? = null

	private fun init(): String {
		/*if (data.isEmpty()) return ""
		if (escapes == null) return data.toString()
		val len = data.length
		val data = data
		var i = 0
		var escIndex = 0
		val res = StringBuilder(len)
		do {
			val escToIndex = if (escIndex >= escapes.size) len else escapes[escIndex++]
			res.append(data, i, escToIndex)
			i = escToIndex + 1
			if (i >= len) break

			val ch = data[i]
			val repl = when (ch) {
				'u' -> 'u'
				'"' -> '"'
				'n' -> '\n'
				'b' -> '\b'
				'/' -> '/'
				'r' -> '\r'
				't' -> '\t'
				else -> ch
			}
			if (repl == 'u') {
				val d = data.subSequence(i + 1, i + 1 + 4).toInt(16)
				res.appendCodePoint(d)
				i += 5 // uXXXX = 5 chars
			} else {
				res.append(repl)
				i++
			}
		} while (i < len)

		return res.toString()*/

		val len = data.length
		var isEscape = false
		var fromIndex = 0
		var i = 0
		val res = StringBuilder(len)
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
					else -> ch
				}
				//if (ch != '-') {
				res.append(data, fromIndex, i - 1)
				if (repl == 'u') {
					val d = data.subSequence(i + 1, i + 1 + 4).toString().toInt(16)
					res.appendCodePoint(d)
					i += 4
				} else {
					res.append(repl)
				}
				fromIndex = i + 1
				//}
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

	override fun equals(other: Any?): Boolean {
		return asString() == other
	}

	override fun hashCode(): Int {
		return asString().hashCode()
	}

	override fun obj(): Any? {
		if (ready == null)
			ready = init()
		return ready
	}

	override fun isPrimitive() = true
}
