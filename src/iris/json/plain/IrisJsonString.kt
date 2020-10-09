package iris.json.plain

import iris.json.JsonString
import iris.json.serialization.Deserializer
import iris.sequence.IrisSequence

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonString(private val data: IrisSequence) : IrisJsonItem(), JsonString {

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append('"')
		data.joinTo(buffer)
		buffer.append('"')
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	private var ready: String? = null// by lazy(LazyThreadSafetyMode.NONE) { init() }

	private fun init(): String {
		val res = StringBuilder()
		val len = data.length
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

	override fun <T : Any> asObject(info: Deserializer): T {
		return return obj() as T
	}

	override fun isPrimitive() = true
}
