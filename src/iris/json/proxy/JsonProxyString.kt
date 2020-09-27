package iris.json.proxy

import iris.json.JsonString

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonProxyString(private val str: String?) : JsonProxyItem(str), JsonString {
	override fun asStringOrNull(): String? {
		return str
	}

	override fun asString(): String {
		return str!!
	}
}