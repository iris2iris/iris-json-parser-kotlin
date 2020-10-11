package iris.json.serialization

import iris.json.JsonItem
import kotlin.reflect.KClass

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface Deserializer {
	fun <T>deserialize(item: JsonItem): T
	fun forSubclass(d: KClass<*>): Deserializer
}

