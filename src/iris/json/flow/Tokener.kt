package iris.json.flow

import iris.json.IrisJson

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface Tokener {

	fun nextChar(): Char

	fun exception(s: String): IllegalArgumentException

	fun readString(quote: Char): CharSequence

	fun readPrimitive(): PrimitiveData

	class PrimitiveData(val sequence: CharSequence, val type: IrisJson.ValueType)

	fun back()

}