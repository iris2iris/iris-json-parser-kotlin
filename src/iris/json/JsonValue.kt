package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonValue : JsonItem {
	override fun isPrimitive() = true

	override fun isNull(): Boolean = false

	override fun isArray(): Boolean = false

	override fun isObject(): Boolean = false
}