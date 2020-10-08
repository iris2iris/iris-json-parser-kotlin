package iris.json

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

typealias JsonEntry = Pair<CharSequence, JsonItem>

interface JsonObject: JsonItem, Iterable<JsonEntry> {
	//class Entry(val key: CharSequence, val value: JsonItem)
}