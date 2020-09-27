package iris.json.proxy

import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.JsonObject.Entry
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
		return JsonProxyUtil.wrap(map[key]) /*when (val item = map[key]) {
			is Map<*, *> -> JsonProxyObject(item as Map<String, Any?>)
			is List<*> -> JsonProxyArray(item as List<Any?>)
			null -> IrisJsonNull.Null
			else -> JsonProxyValue(item)
		}*/
	}

	override fun asMap(): Map<String, Any?> {
		return map
	}

	override fun iterator(): Iterator<Entry> {
		return Iter()
	}

	inner class Iter: Iterator<Entry> {

		private val iterator = map.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): Entry {
			val item = iterator.next()
			val value = when (item.value) {
				is Map<*, *> -> JsonProxyObject(item as Map<String, Any?>)
				is List<*> -> JsonProxyArray(item as List<Any?>)
				null -> IrisJsonNull.Null
				else -> JsonProxyValue(item)
			}
			return Entry(item.key, value)
		}
	}
}