package iris.json.serialization

import iris.json.JsonArray
import iris.json.JsonItem
import kotlin.reflect.KClass

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class DeserializerCollectionImpl : DeserializerCollection {

	lateinit var typeDeserializer: Deserializer

	override fun getObject(items: Collection<JsonItem>): Collection<*> {
		val res = ArrayList<Any?>(items.size)
		for (item in items)
			res.add(typeDeserializer.deserialize(item))
		return res
	}

	override fun <T> deserialize(item: JsonItem): T {
		return getObject((item as JsonArray).getList()) as T
	}

	override fun forSubclass(d: KClass<*>): Deserializer {
		return this
	}
}