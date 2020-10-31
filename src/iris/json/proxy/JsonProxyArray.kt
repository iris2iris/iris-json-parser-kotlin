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
		return JsonProxyUtil.wrap(items[ind])
	}

	override fun get(key: String): IrisJsonItem {
		return get(key.toInt())
	}

	override fun getList(): List<JsonItem> {
		return items.map { JsonProxyUtil.wrap(it) }
	}

	override fun set(ind: Int, value: JsonItem): JsonItem {
		(items as MutableList<Any?>)[ind] = value.obj()
		return this
	}

	override fun set(key: String, value: JsonItem): JsonItem {
		return set(key.toInt(), value)
	}

	override fun asList(): List<Any?> {
		return items
	}

	override val size: Int
		get() = getList().size

	override fun isEmpty() = items.isEmpty()

	override fun isNotEmpty() = items.isNotEmpty()

	override fun isArray() = true

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