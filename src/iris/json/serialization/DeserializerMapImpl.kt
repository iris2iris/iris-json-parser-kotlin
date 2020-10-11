package iris.json.serialization

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import kotlin.reflect.KClass

/**
 * @created 09.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class DeserializerMapImpl : DeserializerMap {

	lateinit var valueDeserializer: Deserializer

	override fun <T> getMap(entries: Collection<JsonEntry>): Map<String, T> {
		return entries.associate {(key, value) ->
			key.toString() to (valueDeserializer.deserialize(value) as T)
		}
	}

	override fun <T> deserialize(item: JsonItem): T {
		return getMap<Map<String, *>>((item as JsonObject).getEntries()) as T
	}

	override fun forSubclass(d: KClass<*>): Deserializer {
		return this
	}
}