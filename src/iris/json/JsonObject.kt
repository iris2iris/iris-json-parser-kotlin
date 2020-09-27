package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonObject: JsonItem, Iterable<JsonObject.Entry> {
	class Entry(val key: CharSequence, val value: JsonItem)
}