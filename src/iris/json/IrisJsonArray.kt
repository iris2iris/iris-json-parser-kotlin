package iris.json

import java.lang.Appendable

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonArray(private val items: List<IrisJsonItem>) : IrisJsonItem(), Iterable<IrisJsonItem> {

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append('[')
		items.joinTo(buffer)
		buffer.append(']')
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return items[ind]
	}

	override fun get(key: String): IrisJsonItem {
		val ind = key.toInt()
		return get(ind)
	}

	private var obj : Any? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		val res = mutableListOf<Any?>()
		for (it in items)
			res.add(it.obj())
		obj = res
		return res
	}

	override fun iterable() = this

	override fun iterator(): Iterator<IrisJsonItem> {
		return Iter()
	}

	private inner class Iter : Iterator<IrisJsonItem> {

		private val size = items.size
		private var pointer = 0

		override fun hasNext(): Boolean {
			return pointer < size
		}

		override fun next(): IrisJsonItem {
			val item = items[pointer]
			pointer++
			return item
		}
	}
}