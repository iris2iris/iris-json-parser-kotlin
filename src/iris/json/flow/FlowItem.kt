package iris.json.flow

import iris.json.plain.IrisJsonItem

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
abstract class FlowItem(protected val tokener: Tokener) : IrisJsonItem() {
	abstract fun parse()
	fun contentSource(start: Int = 0 , end: Int = 0) = tokener.getSourceSequence(start, end)

	override fun toJsonString(): String {
		return appendToJsonString(StringBuilder()).toString()
	}
}