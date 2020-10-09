package iris.json.plain

import iris.json.IrisJson
import iris.json.JsonValue
import iris.json.serialization.Deserializer
import iris.json.serialization.DeserializerPrimitiveImpl
import iris.sequence.*

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
		val s = data
		return when (valueType) {
			IrisJson.ValueType.Constant -> when (s as CharSequence) {
				"null" -> null
				"true" -> true
				"false" -> false
				else -> s.toString()
			}
			IrisJson.ValueType.Integer -> s.toLong()
			IrisJson.ValueType.Float -> s.toDouble()
			//else -> throw IllegalArgumentException("No argument: $valueType")
		}
	}

	override fun asIntOrNull(): Int? {
		if (done)
			return super.asIntOrNull()
		val res = data.toIntOrNull()
		ready = res
		done = true
		return res
	}

	override fun asInt(): Int {
		if (done)
			return super.asInt()
		val res = data.toInt()
		ready = res
		done = true
		return res
	}

	override fun asLongOrNull(): Long? {
		if (done)
			return super.asLongOrNull()
		val res = data.toLongOrNull()
		ready = res
		done = true
		return res
	}

	override fun asLong(): Long {
		if (done)
			return super.asLong()
		val res = data.toLong()
		ready = res
		done = true
		return res
	}

	override fun asDoubleOrNull(): Double? {
		if (done)
			return super.asDoubleOrNull()
		val res = data.toDoubleOrNull()
		ready = res
		done = true
		return res
	}

	override fun asDouble(): Double {
		if (done)
			return super.asDouble()
		val res = data.toDouble()
		ready = res
		done = true
		return res
	}

	override fun asFloatOrNull(): Float? {
		if (done)
			return super.asFloatOrNull()
		val res = data.toFloatOrNull()
		ready = res
		done = true
		return res
	}

	override fun asFloat(): Float {
		if (done)
			return super.asFloat()
		val res = data.toFloat()
		ready = res
		done = true
		return res
	}

	private var ready: Any? = null
	private var done = false

	override fun obj(): Any? {
		if (done)
			return ready
		ready = init()
		done = true
		return ready
	}

	override fun <T : Any> asObject(info: Deserializer): T {
		return (info as DeserializerPrimitiveImpl).getValue(this) as T
	}

	override fun isPrimitive() = true
}