package iris.json.plain

import iris.json.JsonNull

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

	override fun <A : Appendable> appendToJsonString(buffer: A): A {
		buffer.append("null")
		return buffer
	}

	override fun toString(): String {
		return "null"
	}

	override fun isNull() = true
}