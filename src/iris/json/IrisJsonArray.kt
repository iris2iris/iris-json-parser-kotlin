package iris.json

import java.lang.Appendable

class IrisJsonArray(private val items: List<IrisJsonItem>) : IrisJsonItem(IrisJson.Type.Array) {

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
}