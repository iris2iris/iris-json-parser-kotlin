package iris.json.proxy

import iris.json.JsonValue

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonProxyValue(value: Any?) : JsonProxyItem(value), JsonValue {
	override fun isPrimitive() = true
}