package iris.json.proxy

import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonNull

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
abstract class JsonProxyItem(private val obj: Any?) : IrisJsonItem() {
	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun obj(): Any? {
		return obj
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append(obj.toString())
		return buffer
	}
}