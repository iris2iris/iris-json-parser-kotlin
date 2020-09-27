package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonArray: JsonItem, Iterable<JsonItem> {
	override fun isArray() = true

	override fun isNull(): Boolean = false

	override fun isPrimitive(): Boolean = false

	override fun isObject(): Boolean = false
}