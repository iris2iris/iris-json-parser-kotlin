package iris.json.serialization

import iris.json.JsonEntry

/**
 * @created 09.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class DeserializerMapImpl(val valueDeserializer: Deserializer) : DeserializerMap {
	override fun <T> getMap(entries: List<JsonEntry>): Map<String, T> {
		return entries.associate {(key, value) ->
			key.toString() to (value.asObject(valueDeserializer) as T)
		}
	}

	override fun <T: Any> getObject(entries: List<JsonEntry>): T {
		return getMap<Map<String, *>>(entries) as T
	}
}