package iris.json.serialization

import iris.json.JsonArray
import iris.json.JsonItem

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class DeserializerCollectionImpl(val typeDeserializer: Deserializer) : DeserializerCollection {
	override fun getObject(items: Collection<JsonItem>): Collection<*> {
		val res = mutableListOf<Any?>()
		for (item in items)
			res.add(typeDeserializer.deserialize(item))
		return res
	}

	override fun <T> deserialize(item: JsonItem): T {
		return getObject((item as JsonArray).getList()) as T
	}
}