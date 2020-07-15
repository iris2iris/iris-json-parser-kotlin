package iris.json

import IrisJsonItem
import java.lang.Appendable

class IrisJsonObject(private val entries: List<Entry>) : IrisJsonItem(IrisJson.Type.Object) {

	class Entry(val key: IrisSequence, val value: IrisJsonItem) {
		override fun toString(): String {
			return "\"$key\": $value"
		}
	}

	override fun toString(): String {
		return "{" + entries.joinToString { it.toString() } + "}"
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append("{")
		var firstDone = false
		for (entry in entries) {
			if (firstDone)
				buffer.append(", ")
			else
				firstDone = true
			buffer.append("\"")
			buffer.append(entry.key)
			buffer.append("\": ")
			entry.value.joinTo(buffer)

		}
		buffer.append('}')
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return get(ind.toString())
	}

	private val map by lazy(LazyThreadSafetyMode.NONE) { init() }

	private fun init(): MutableMap<String, IrisJsonItem> {
		val t = mutableMapOf<String, IrisJsonItem>()
		for (it in entries) {
			t[it.key.toString()] = it.value
		}
		return t
	}

	override fun get(key: String): IrisJsonItem {
		return map[key] ?: IrisJsonNull.Null
	}

	override fun obj(): Any? {
		return map
	}
}