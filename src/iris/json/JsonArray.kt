package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonArray: JsonItem, Iterable<JsonItem> {
	fun getList(): List<JsonItem>
	val size: Int
	fun isEmpty(): Boolean
	fun isNotEmpty(): Boolean
}