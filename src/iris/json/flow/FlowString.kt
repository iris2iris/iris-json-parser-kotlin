package iris.json.flow

import iris.json.JsonString
import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonNull
import iris.sequence.IrisSequence

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowString(tokener: Tokener, val quote: Char) : FlowItem(tokener), JsonString {

	private var data: CharSequence? = null

	override fun <A : Appendable> appendToJsonString(buffer: A): A {
		parse()
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
		if (data == null)
			data = tokener.readString(quote)
	}

	private var ready: String? = null// by lazy(LazyThreadSafetyMode.NONE) { init() }

	private fun init(): String {
		parse()
		val data = data!!
		val len = data.length
		val res = StringBuilder(len)
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
			if (fromIndex != len)
				res.append(data, fromIndex, len)
			res.toString()
		}
	}

	override fun obj(): String {
		if (ready != null)
			return ready!!
		ready = init()
		return ready!!
	}

	override fun isPrimitive() = true
}
