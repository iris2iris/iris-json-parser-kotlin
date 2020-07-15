package iris.json

import IrisJsonItem
import java.lang.Appendable

class IrisJsonArray(private val items: List<IrisJsonItem>) : IrisJsonItem(IrisJson.Type.Array) {
	override fun toString(): String {
		return '[' + items.joinToString { it.toString() } + ']'
	}

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

	override fun obj(): Any? {
		return items
	}
}