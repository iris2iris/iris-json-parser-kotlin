package iris.json.plain

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.proxy.JsonProxyUtil
import iris.json.serialization.Deserializer
import iris.json.serialization.DeserializerClass

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
open class IrisJsonObject(private val entries: List<JsonEntry>) : IrisJsonItem(), JsonObject {

	constructor(vararg items: Pair<String, JsonItem>) : this(items.asList())

	override fun get(key: String): JsonItem {
		return (entries.find {it.first == key }?.second) ?: IrisJsonNull.Null
	}

	override fun get(ind: Int): JsonItem {
		return get(ind.toString())
	}

	override fun set(ind: Int, value: Any?): JsonItem {
		return set(ind.toString(), value)
	}

	override fun set(key: String, value: Any?): JsonItem {
		val el = entries as MutableList<JsonEntry>
		val index = el.indexOfFirst { it.first == key }
		val wrapValue = JsonProxyUtil.wrap(value)
		if (index == -1)
			el += JsonEntry(key, wrapValue)
		else
			el[index] = JsonEntry(key, wrapValue)
		val obj = obj
		if (obj != null) {
			obj[key] = value
		}
		return this
	}

	private var obj: MutableMap<String, Any?>? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		val res = HashMap<String, Any?>(entries.size)
		for (it in entries)
			res[it.first.toString()] = it.second.obj()
		obj = res
		return res
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
			buffer.append(entry.first)
			buffer.append("\": ")
			entry.second.joinTo(buffer)

		}
		buffer.append('}')
		return buffer
	}

	override fun isObject() = true

	override fun iterator(): Iterator<JsonEntry> {
		return Iter()
	}

	inner class Iter: Iterator<JsonEntry> {

		private val iterator = entries.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): JsonEntry {
			return iterator.next()
		}
	}

	override fun <T: Any>asObject(info: Deserializer): T {
		return (info as DeserializerClass).getObject(entries)
	}
}


