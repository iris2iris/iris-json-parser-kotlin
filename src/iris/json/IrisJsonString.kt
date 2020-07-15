package iris.json

import java.lang.Appendable

class IrisJsonString(private val data: IrisSequence) : IrisJsonItem(IrisJson.Type.String) {

	override fun toString(): String {
		return '"' + data.toString() + '"'
	}

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

	private val ready by lazy(LazyThreadSafetyMode.NONE) { init() }

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

	override fun obj(): Any? {
		return ready
	}

	/*fun testSuper() {
		for (i in 1..1_000)
			init()
		System.gc()
		val start = System.currentTimeMillis()
		for (i in 1..1_000_000)
			init()
		val end = System.currentTimeMillis()
		println("Reg: " + (end - start) + " ms")
	}

	private val uSeq = Regex("\\\\u([a-fA-F\\d]{4})")

	fun testReg() {
		for (i in 1..1_000)
			data.toString().replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
					.replace("\\/", "/")
					.replace("\\b", "\b")
					.replace(uSeq) { it.groupValues[1].toInt(16).toChar().toString() }
		System.gc()
		val start = System.currentTimeMillis()
		for (i in 1..1_000_000)
			data.toString().replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
					.replace("\\/", "/")
					.replace("\\b", "\b")
					.replace(uSeq) { it.groupValues[1].toInt(16).toChar().toString() }
		val end = System.currentTimeMillis()
		println("Reg: " + (end - start) + " ms")
	}*/
}
