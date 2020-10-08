package iris.json.serialization

import iris.json.JsonItem

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class ListInfo(val type: NodeInfo) : NodeInfo {
	fun getObject(items: List<JsonItem>): Collection<*> {
		val res = mutableListOf<Any?>()
		for (item in items)
			res.add(item.asObject(type))
		return res
	}
}