package iris.json.plain

import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.JsonObject.Entry
import iris.json.proxy.JsonProxyUtil

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonObject(private val entries: List<Entry>) : IrisJsonItem(), JsonObject {

	constructor(vararg items: Pair<CharSequence, JsonItem>) : this(items.map { Entry(it.first, it.second) })

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

	override fun set(ind: Int, value: Any?): JsonItem {
		return set(ind.toString(), value)

	}

	override fun set(key: String, value: Any?): JsonItem {
		val el = entries as MutableList<Entry>
		val index = el.indexOfFirst { it.key == key }
		val wrapValue = JsonProxyUtil.wrap(value)
		if (index == -1)
			el += Entry(key, wrapValue)
		else
			el[index] = Entry(key, wrapValue)
		val obj = obj
		if (obj != null) {
			obj[key] = value
		}
		return this
	}

	override fun get(ind: Int): IrisJsonItem {
		return get(ind.toString())
	}

	/*private val map by lazy(LazyThreadSafetyMode.NONE) { init() }

	private fun init(): MutableMap<String, IrisJsonItem> {
		val t = mutableMapOf<String, IrisJsonItem>()
		for (it in entries) {
			t[it.key.toString()] = it.value as IrisJsonItem
		}
		return t
	}*/

	override fun get(key: String): IrisJsonItem {

		return (entries.find { it.key == key }?.value as IrisJsonItem?) ?: IrisJsonNull.Null
		//return map[key] ?: IrisJsonNull.Null
	}

	private var obj: MutableMap<String, Any?>? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		val res = mutableMapOf<String, Any?>()
		//for (it in map )
		for (it in entries)
			res[it.key.toString()] = it.value.obj()
		obj = res
		return res
	}

	override fun isObject() = true

	override fun iterator(): Iterator<Entry> {
		return Iter()
	}

	inner class Iter: Iterator<Entry> {

		private val iterator = entries.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): Entry {
			return iterator.next()
		}
	}
}