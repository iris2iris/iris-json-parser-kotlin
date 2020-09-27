package iris.json.proxy

import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonNull

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
object JsonProxyUtil {
	fun wrap(value: Any?): IrisJsonItem {
		return when (value) {
			is Map<*, *> -> JsonProxyObject(value as Map<String, Any?>)
			is List<*> -> JsonProxyArray(value as List<Any?>)
			null -> IrisJsonNull.Null
			else -> JsonProxyValue(value)
		}
	}
}