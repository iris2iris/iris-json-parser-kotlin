package iris.json.proxy

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonNull
import iris.json.serialization.NodeInfo

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonProxyObject(private val map: Map<String, Any?>) : JsonProxyItem(map), JsonObject {

	constructor(vararg items: Pair<String, Any?>) : this(LinkedHashMap<String, Any?>(items.size).apply { this.putAll(items) })

	override fun get(ind: Int): IrisJsonItem {
		return get(ind.toString())
	}

	override operator fun set(key: String, value: Any?): JsonItem {
		if (map is MutableMap<*, *>)
			(map as MutableMap<String, Any?>)[key] = value
		else
			throw IllegalStateException("Source map object is not mutable")
		return this
	}

	override fun set(ind: Int, value: Any?): JsonItem {
		return set(ind.toString(), value)
	}

	override fun get(key: String): IrisJsonItem {
		return JsonProxyUtil.wrap(map[key])
	}

	override fun asMap(): Map<String, Any?> {
		return map
	}

	override fun <T : Any> asObject(info: NodeInfo): T {
		return map as T
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