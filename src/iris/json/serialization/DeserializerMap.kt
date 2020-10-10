package iris.json.serialization

import iris.json.JsonEntry

/**
 * @created 09.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface DeserializerMap : Deserializer {
	fun <T>getMap(entries: Collection<JsonEntry>): Map<String, T>
}