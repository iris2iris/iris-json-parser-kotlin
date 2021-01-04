package iris.json.plain

import iris.json.JsonArray
import iris.json.JsonItem

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonArray(private val items: List<JsonItem>) : IrisJsonItem(), JsonArray {

	override fun <A : Appendable> appendToJsonString(buffer: A): A {
		buffer.append('[')
		items.joinTo(buffer)
		buffer.append(']')
		return buffer
	}

	override fun get(ind: Int): JsonItem {
		return items[ind]
	}

	override fun get(key: String): JsonItem {
		val ind = key.toInt()
		return get(ind)
	}

	override fun getList(): List<JsonItem> {
		return items
	}

	override val size: Int
		get() = getList().size

	override fun isEmpty() = items.isEmpty()

	override fun isNotEmpty() = items.isNotEmpty()

	override fun set(ind: Int, value: JsonItem): JsonItem {
		(items as MutableList<Any?>)[ind] = value
		val obj = this.obj
		if (obj != null)
			obj[ind] = value.obj()
		return this
	}

	override fun set(key: String, value: JsonItem): JsonItem {
		return set(key.toInt(), value)
	}

	private var obj : MutableList<Any?>? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		val res = mutableListOf<Any?>()
		for (it in items)
			res.add(it.obj())
		obj = res
		return res
	}

	override fun isArray() = true

	override fun iterable() = this

	override fun iterator(): Iterator<JsonItem> {
		return Iter()
	}

	private inner class Iter : Iterator<JsonItem> {

		private val size = items.size
		private var pointer = 0

		override fun hasNext(): Boolean {
			return pointer < size
		}

		override fun next(): JsonItem {
			val item = items[pointer]
			pointer++
			return item
		}
	}
}