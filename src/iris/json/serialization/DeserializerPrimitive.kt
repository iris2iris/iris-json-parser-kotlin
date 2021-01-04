package iris.json.serialization

import iris.json.JsonItem

/**
 * @created 09.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface DeserializerPrimitive : Deserializer {
	fun getValue(item: JsonItem): Any?

	enum class Type {
		ANY, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, STRING, DATE
	}
}