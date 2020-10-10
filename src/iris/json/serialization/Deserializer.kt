package iris.json.serialization

import iris.json.JsonItem

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface Deserializer {
	fun <T>deserialize(item: JsonItem): T
}

