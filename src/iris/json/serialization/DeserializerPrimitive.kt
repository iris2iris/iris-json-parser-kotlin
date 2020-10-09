package iris.json.serialization

import iris.json.JsonItem

/**
 * @created 09.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
internal interface DeserializerPrimitive : Deserializer {
	fun getValue(value: JsonItem): Any?
}