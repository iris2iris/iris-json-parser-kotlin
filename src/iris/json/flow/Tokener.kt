package iris.json.flow

import iris.json.IrisJson

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface Tokener {

	fun nextChar(): Char

	fun exception(s: String): IllegalArgumentException

	fun getSourceSequence(start: Int = 0, end: Int = 0): CharSequence

	fun readString(quote: Char): CharSequence

	fun readFieldName(quote: Char?): CharSequence

	fun readPrimitive(): PrimitiveData

	class PrimitiveData(val sequence: CharSequence, val type: IrisJson.ValueType)

	fun back()

}