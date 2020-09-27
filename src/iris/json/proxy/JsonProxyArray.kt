package iris.json.proxy

import iris.json.JsonArray
import iris.json.JsonItem
import iris.json.plain.IrisJsonItem

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonProxyArray(private val items: List<Any?>) : JsonProxyItem(items), JsonArray {

	override fun get(ind: Int): IrisJsonItem {
		return JsonProxyUtil.wrap(items[ind]) /*when (val item = items[ind]) {
			is Map<*, *> -> JsonProxyObject(item as Map<String, Any?>)
			is List<*> -> JsonProxyArray(item as List<Any?>)
			null -> IrisJsonNull.Null
			else -> JsonProxyValue(item)
		}*/
	}

	override fun get(key: String): IrisJsonItem {
		return get(key.toInt())
	}

	override fun set(ind: Int, value: Any?): JsonItem {
		(items as MutableList<Any?>)[ind] = value
		return this
	}

	override fun set(key: String, value: Any?): JsonItem {
		return set(key.toInt(), value)
	}

	override fun asList(): List<Any?> {
		return items
	}

	override fun <T> asTypedList(): List<T> {
		return items as List<T>
	}

	override fun iterator() = Iter()

	inner class Iter : Iterator<JsonItem> {

		private val iterator = items.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): JsonItem {
			return JsonProxyUtil.wrap(iterator.next())
		}
	}
}