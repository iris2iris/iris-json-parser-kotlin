package iris.json

import iris.sequence.IrisSequence
import java.lang.Appendable

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonObject(private val entries: List<Entry>) : IrisJsonItem() {

	class Entry(val key: IrisSequence, val value: IrisJsonItem) {
		override fun toString(): String {
			return "\"$key\": $value"
		}
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
			entry.key.joinTo(buffer)
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

	private var obj: Any? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		val res = mutableMapOf<String, Any?>()
		for (it in map )
			res[it.key] = it.value.obj()
		obj = res
		return res
	}
}