package iris.json

import java.lang.Appendable
import java.lang.Exception

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
abstract class IrisJsonItem() {
	abstract operator fun get(ind: Int): IrisJsonItem
	abstract operator fun get(key: String): IrisJsonItem
	abstract fun obj(): Any?
	abstract fun <A: Appendable>joinTo(buffer: A): A

	override fun toString(): String {
		return joinTo(StringBuilder()).toString()
	}

	open fun iterable() : Iterable<IrisJsonItem> {
		throw IllegalStateException("This is not iterable json item")
	}
	
	open fun asIntOrNull(): Int? {
		return when (val obj = obj()) {
			is Int -> obj
			is Number -> obj.toInt()
			else -> null
		}
	}

	open fun asInt(): Int {
		try {
			return when (val obj = obj()) {
				is Int -> obj
				else -> (obj() as Number).toInt()
			}
		} catch (e: Exception) {
			e.printStackTrace()
			throw e
		}
	}

	open fun asLongOrNull() : Long? {
		return when (val obj = obj()) {
			is Long -> obj
			is Number -> obj.toLong()
			else -> null
		}
	}

	open fun asLong() : Long {
		return when (val obj = obj()) {
			is Long -> obj
			else -> (obj() as Number).toLong()
		}
	}

	open fun asDoubleOrNull() : Double? {
		return when (val obj = obj()) {
			is Double -> obj
			is Number -> obj.toDouble()
			else -> null
		}
	}

	open fun asDouble() : Double {
		return when (val obj = obj()) {
			is Double -> obj
			else -> (obj() as Number).toDouble()
		}
	}

	open fun asFloatOrNull() : Float? {
		return when (val obj = obj()) {
			is Float -> obj
			is Number -> obj.toFloat()
			else -> null
		}
	}

	open fun asFloat() : Float {
		return when (val obj = obj()) {
			is Float -> obj
			else -> (obj() as Number).toFloat()
		}
	}

	open fun asBooleanOrNull() : Boolean? {
		return when (val obj = obj()) {
			is Boolean -> obj
			else -> null
		}
	}

	open fun asBoolean() : Boolean {
		return (obj() as Boolean)
	}

	open fun asList(): List<Any?> {
		return when (val obj = obj()) {
			is List<*> -> obj
			else -> (obj as Iterable<*>).toList()
		}
	}

	open fun <T>asTypedList(): List<T> {
		return when (val obj = obj()) {
			is List<*> -> obj as List<T>
			else -> (obj as Iterable<*>).toList() as List<T>
		}
	}

	open fun asMap(): Map<String, Any?> {
		return (obj() as Map<String, Any?>)
	}

	open fun asStringOrNull(): String? {
		return obj() as String?
	}

	open fun asString(): String {
		return obj() as String
	}

	open fun find(tree: Array<String>): IrisJsonItem {
		var cur = this
		for (t in tree)
			cur = cur[t]
		return cur
	}

	open fun find(tree: List<String>): IrisJsonItem {
		var cur = this
		for (t in tree) {
			if (t.isEmpty()) continue
			cur = cur[t]
		}
		return cur
	}

	open fun find(tree: String): IrisJsonItem {
		return find(tree.replace('[', '.').replace("]", "").replace(' ', '.').split('.'))
	}


}