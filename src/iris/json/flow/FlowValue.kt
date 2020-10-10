package iris.json.flow

import iris.json.IrisJson
import iris.json.JsonValue
import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonNull
import iris.sequence.*

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowValue(tokener: Tokener) : FlowItem(tokener), JsonValue {

	private var data: Tokener.PrimitiveData? = null

	override fun <A : Appendable> joinTo(buffer: A): A {
		parse()
		buffer.append(data!!.sequence)
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	private fun init(): Any? {
		parse()
		val data = data!!
		val s = data.sequence
		return when (data.type) {
            IrisJson.ValueType.Constant -> when (s) {
                "null" -> null
                "true", "1" -> true
                "false", "0" -> false
                else -> s.toString()
            }
            IrisJson.ValueType.Integer -> s.toLong()
            IrisJson.ValueType.Float -> s.toDouble()
			else -> throw IllegalArgumentException("No argument: ${data.type}")
		}
	}

	override fun asIntOrNull(): Int? {
		if (done)
			return super.asIntOrNull()
		parse()
		val res = data!!.sequence.toIntOrNull()
		ready = res
		done = true
		return res
	}

	override fun asInt(): Int {
		if (done)
			return super.asInt()
		parse()
		val res = data!!.sequence.toInt()
		ready = res
		done = true
		return res
	}

	override fun asLongOrNull(): Long? {
		if (done)
			return super.asLongOrNull()
		parse()
		val res = data!!.sequence.toLongOrNull()
		ready = res
		done = true
		return res
	}

	override fun asLong(): Long {
		if (done)
			return super.asLong()
		parse()
		val res = data!!.sequence.toLong()
		ready = res
		done = true
		return res
	}

	override fun asDoubleOrNull(): Double? {
		if (done)
			return super.asDoubleOrNull()
		parse()
		val res = data!!.sequence.toDoubleOrNull()
		ready = res
		done = true
		return res
	}

	override fun asDouble(): Double {
		if (done)
			return super.asDouble()
		parse()
		val res = data!!.sequence.toDouble()
		ready = res
		done = true
		return res
	}

	override fun asFloatOrNull(): Float? {
		if (done)
			return super.asFloatOrNull()
		parse()
		val res = data!!.sequence.toFloatOrNull()
		ready = res
		done = true
		return res
	}

	override fun asFloat(): Float {
		if (done)
			return super.asFloat()
		parse()
		val res = data!!.sequence.toFloat()
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

	override fun parse() {
		if (data != null)
			return
		data = this.tokener.readPrimitive()
	}

	override fun isPrimitive() = true
}