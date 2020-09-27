package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonObject: JsonItem, Iterable<JsonObject.Entry> {
	override fun isObject() = true

	override fun isNull(): Boolean = false
	override fun isPrimitive(): Boolean = false
	override fun isArray(): Boolean = false

	data class Entry(val key: CharSequence, val value: JsonItem)


}