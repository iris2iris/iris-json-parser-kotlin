package iris.json.serialization

import iris.json.JsonItem

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class DeserializerCollectionImpl(val type: Deserializer) : DeserializerCollection {
	override fun getObject(items: Collection<JsonItem>): Collection<*> {
		val res = mutableListOf<Any?>()
		for (item in items)
			res.add(item.asObject(type))
		return res
	}
}