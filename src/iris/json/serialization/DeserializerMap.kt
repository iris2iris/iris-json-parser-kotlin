package iris.json.serialization

import iris.json.JsonEntry

/**
 * @created 09.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface DeserializerMap : DeserializerClass {
	fun <T>getMap(entries: List<JsonEntry>): Map<String, T>
	//fun <T>getObject(entries: List<JsonEntry>): T
}