package iris.json.plain

import iris.json.JsonItem
import iris.json.serialization.SerializationCache
import kotlin.reflect.KClass

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
abstract class IrisJsonItem() : JsonItem {

	override fun iterable(): Iterable<JsonItem> {
		throw IllegalStateException("This is not iterable json item")
	}

	override fun asIntOrNull(): Int? {
		return when (val obj = obj()) {
			is Int -> obj
			is Number -> obj.toInt()
			else -> null
		}
	}

	override fun asInt(): Int {
		return when (val obj = obj()) {
			is Int -> obj
			else -> (obj() as Number).toInt()
		}
	}

	override fun asLongOrNull(): Long? {
		return when (val obj = obj()) {
			is Long -> obj
			is Number -> obj.toLong()
			else -> null
		}
	}

	override fun asLong(): Long {
		return when (val obj = obj()) {
			is Long -> obj
			else -> (obj() as Number).toLong()
		}
	}

	override fun asDoubleOrNull(): Double? {
		return when (val obj = obj()) {
			is Double -> obj
			is Number -> obj.toDouble()
			else -> null
		}
	}

	override fun asDouble(): Double {
		return when (val obj = obj()) {
			is Double -> obj
			else -> (obj() as Number).toDouble()
		}
	}

	override fun asFloatOrNull(): Float? {
		return when (val obj = obj()) {
			is Float -> obj
			is Number -> obj.toFloat()
			else -> null
		}
	}

	override fun asFloat(): Float {
		return when (val obj = obj()) {
			is Float -> obj
			else -> (obj() as Number).toFloat()
		}
	}

	override fun asBooleanOrNull(): Boolean? {
		return when (val obj = obj()) {
			is Boolean -> obj
			else -> null
		}
	}

	override fun asBoolean(): Boolean {
		return (obj() as Boolean)
	}

	override fun asList(): List<Any?> {
		return when (val obj = obj()) {
			is List<*> -> obj
			else -> (obj as Iterable<*>).toList()
		}
	}

	override fun <T> asTypedList(): List<T> {
		return when (val obj = obj()) {
			is List<*> -> obj as List<T>
			else -> (obj as Iterable<*>).toList() as List<T>
		}
	}

	override fun asMap(): Map<String, Any?> {
		return (obj() as Map<String, Any?>)
	}

	override fun asStringOrNull(): String? {
		return obj() as String?
	}

	override fun asString(): String {
		return obj() as String
	}

	override fun find(tree: Array<String>): JsonItem {
		var cur: JsonItem = this
		for (t in tree) {
			if (t.isEmpty()) continue
			cur = cur[t]
		}
		return cur
	}

	override fun find(tree: List<String>): JsonItem {
		var cur: JsonItem = this
		for (t in tree) {
			if (t.isEmpty()) continue
			cur = cur[t]
		}
		return cur
	}

	override fun find(tree: String): JsonItem {
		return find(tree.replace('[', '.').replace("]", "").replace(' ', '.').split('.'))
	}

	override fun set(ind: Int, value: Any?): JsonItem {
		throw IllegalStateException("Set operation is not available")
	}

	override fun set(key: String, value: Any?): JsonItem {
		throw IllegalStateException("Set operation is not available")
	}

	override fun equals(other: Any?): Boolean {
		return when (other) {
			null -> false
			is JsonItem -> obj() == other.obj()
			else -> obj() == other
		}
	}

	override fun isNull() = false

	override fun isNotNull() = !isNull()

	override fun isPrimitive() = false

	override fun isArray() = false

	override fun isObject() = false

	override fun <T : Any> asObject(d: KClass<T>): T {
		return asObject(SerializationCache.getInstance(d))
	}
}