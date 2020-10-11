package iris.json.serialization

import iris.json.JsonItem
import kotlin.reflect.KClass

/**
 * @created 10.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class DeserializerJsonItem : Deserializer {
	override fun <T> deserialize(item: JsonItem): T {
		return item as T
	}

	override fun forSubclass(d: KClass<*>): Deserializer {
		return this
	}
}