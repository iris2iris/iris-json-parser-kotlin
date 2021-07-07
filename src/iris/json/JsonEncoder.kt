package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
object JsonEncoder {

	fun encode(obj: Any?, escapeUtf: Boolean = false): String {
		val sb = StringBuilder()
		encode(obj, sb, escapeUtf)
		return sb.toString()
	}

	fun encode(obj: Any?, sb: StringBuilder, escapeUtf: Boolean = false) {
		when (obj) {
			null -> sb.append("null")
			is Map<*, *> -> encode2(obj as Map<Any, Any?>, sb, escapeUtf)
			is Collection<*> -> array2String(obj, sb, escapeUtf)
			is Array<*> -> array2String(obj, sb, escapeUtf)
			else -> value2JsonString(obj, sb, escapeUtf)
		}
	}

	private fun encode2(obj: Map<Any, Any?>?, sb: StringBuilder, escapeUtf: Boolean = false) {
		if (obj == null) {
			sb.append("null")
			return
		}

		var first = true
		sb.append('{')

		for (entry in obj.entries) {
			if (first)
				first = false
			else
				sb.append(',')
			object2String(entry.key.toString(), entry.value, sb, escapeUtf)
		}

		sb.append('}')
	}

	private fun object2String(key: String?, value: Any?, sb: StringBuilder, escapeUtf: Boolean = false) {
		sb.append('"')
		if (key == null)
			sb.append("null")
		else
			escape(key, sb, escapeUtf)
		sb.append("\":")

		value2JsonString(value, sb, escapeUtf)
	}

	private fun escape(s: CharSequence, sb: StringBuilder, escapeUtf: Boolean/* = false*/) {
		val arr = s.toString().toCharArray()
		for (ch in arr) {
			//val ch = s[i]
			when (ch) {
				'"' -> sb.append("\\\"")
				'\\' -> sb.append("\\\\")
				'\b' -> sb.append("\\b")
				//'\f' -> sb.append("\\f")
				'\n' -> sb.append("\\n")
				'\r' -> sb.append("\\r")
				'\t' -> sb.append("\\t")
				'/' -> sb.append("\\/")
				else ->
					//Reference: http://www.unicode.org/versions/Unicode5.1.0/
					if (escapeUtf && (ch in '\u0000'..'\u001F' || ch in '\u007F'..'\u009F' || ch in '\u2000'..'\u20FF')) {
						val ss = Integer.toHexString(ch.toInt())
						sb.append("\\u")
						for (k in 0 until 4 - ss.length) {
							sb.append('0')
						}
						sb.append(ss.toUpperCase())
					} else {
						sb.append(ch)
					}
			}
		}//for
	}

	private fun value2JsonString(value: Any?, sb: StringBuilder, escapeUtf: Boolean) {
		if (value == null) {
			sb.append("null")
			return
		}

		if (value is CharSequence) {
			sb.append('"'); escape(value, sb, escapeUtf); sb.append('"')
			return
		}

		if (value is Number) {
			sb.append(value.toString())
			return
		}

		if (value is Map<*, *>) {
			encode2(value as Map<Any, Any?>, sb)
			return
		}

		if (value is List<*>) {
			array2String(value, sb, escapeUtf)
			return
		}

		if (value is Array<*>) {
			array2String(value, sb, escapeUtf)
			return
		}

		sb.append(value.toString())

	}

	private fun array2String(list: Collection<*>?, sb: StringBuilder, escapeUtf: Boolean) {
		if (list == null) {
			sb.append("null")
			return
		}

		var first = true
		sb.append('[')

		for (value in list) {
			if (first)
				first = false
			else
				sb.append(',')
			value2JsonString(value, sb, escapeUtf)
		}
		sb.append(']')
	}

	private fun array2String(list: Array<*>?, sb: StringBuilder, escapeUtf: Boolean) {
		if (list == null) {
			sb.append("null")
			return
		}

		var first = true

		sb.append('[')
		for (value in list) {
			if (first)
				first = false
			else
				sb.append(',')
			value2JsonString(value, sb, escapeUtf)
		}
		sb.append(']')
	}
}