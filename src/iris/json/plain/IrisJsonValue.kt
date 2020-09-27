package iris.json.plain

import iris.json.IrisJson
import iris.json.JsonValue
import iris.sequence.IrisSequence

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonValue(private val data: IrisSequence, private val valueType: IrisJson.ValueType) : IrisJsonItem(), JsonValue {
	override fun toString(): String {
		return data.toString()
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		data.joinTo(buffer)
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	private fun init(): Any? {
		val s = data.toString()
		return when (valueType) {
			IrisJson.ValueType.Constant -> when (s) {
				"null" -> null
				"true" -> true
				"false" -> false
				else -> s
			}
			IrisJson.ValueType.Integer -> s.toLong()
			IrisJson.ValueType.Float -> s.toDouble()
			else -> throw IllegalArgumentException("No argument: $valueType")
		}
	}

	private val ready by lazy(LazyThreadSafetyMode.NONE) { init() }

	override fun obj(): Any? {
		return ready
	}

	override fun isPrimitive() = true
}