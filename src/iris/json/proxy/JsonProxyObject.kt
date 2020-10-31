package iris.json.proxy

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonNull

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonProxyObject(private val map: Map<String, Any?>) : JsonProxyItem(map), JsonObject {

	constructor(vararg items: Pair<String, Any?>) : this(LinkedHashMap<String, Any?>(items.size).apply { this.putAll(items) })

	override fun get(ind: Int): IrisJsonItem {
		return get(ind.toString())
	}

	override operator fun set(key: String, value: JsonItem): JsonItem {
		if (map is MutableMap<*, *>)
			(map as MutableMap<String, Any?>)[key] = value.obj()
		else
			throw IllegalStateException("Source map object is not mutable")
		return this
	}

	override fun set(ind: Int, value: JsonItem): JsonItem {
		return set(ind.toString(), value)
	}

	override fun get(key: String): IrisJsonItem {
		return JsonProxyUtil.wrap(map[key])
	}

	override fun asMap(): Map<String, Any?> {
		return map
	}

	override fun getEntries(): Collection<JsonEntry> {
		return map.map { it.key to JsonProxyUtil.wrap(it.value) }
	}

	override fun isObject() = true

	override fun iterator(): Iterator<JsonEntry> {
		return Iter()
	}

	inner class Iter: Iterator<JsonEntry> {

		private val iterator = map.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): JsonEntry {
			val item = iterator.next()
			val value = when (item.value) {
				is Map<*, *> -> JsonProxyObject(item as Map<String, Any?>)
				is List<*> -> JsonProxyArray(item as List<Any?>)
				null -> IrisJsonNull.Null
				else -> JsonProxyValue(item)
			}
			return JsonEntry(item.key, value)
		}
	}
}