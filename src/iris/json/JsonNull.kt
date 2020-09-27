package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonNull : JsonItem {
	override fun isNull() = true

	override fun isPrimitive(): Boolean = false

	override fun isArray(): Boolean = false

	override fun isObject(): Boolean = false
}