package iris.json.plain

import iris.json.JsonNull
import iris.json.serialization.NodeInfo

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
open class IrisJsonNull : IrisJsonItem(), JsonNull {

	companion object {
		val Null = IrisJsonNull()
	}

	override fun get(ind: Int): IrisJsonItem {
		return this
	}

	override fun get(key: String): IrisJsonItem {
		return this
	}

	override fun obj(): Any? {
		return null
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append("null")
		return buffer
	}

	override fun toString(): String {
		return "null"
	}

	override fun <T : Any> asObject(info: NodeInfo): T {
		return null as T
	}

	override fun isNull() = true
}