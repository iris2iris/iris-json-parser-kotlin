package iris.json

import java.lang.Appendable

abstract class IrisJsonItem(val type: IrisJson.Type) {
	abstract operator fun get(ind: Int): IrisJsonItem
	abstract operator fun get(key: String): IrisJsonItem
	abstract fun obj(): Any?
	abstract fun <A: Appendable>joinTo(buffer: A): A

	override fun toString(): String {
		return joinTo(StringBuilder()).toString()
	}

	open fun asInt() : Int? {
		return (obj() as Number?)?.toInt()
	}

	open fun asLong() : Long? {
		return (obj() as Number?)?.toLong()
	}

	open fun asDouble() : Double? {
		return (obj() as Number?)?.toDouble()
	}

	open fun asFloat() : Float? {
		return (obj() as Number?)?.toFloat()
	}

	open fun asBoolean() : Boolean? {
		return (obj() as Boolean?)
	}

	open fun asList(): List<Any?> {
		return (obj() as Collection<Any?>).toList()
	}

	open fun asObject(): Map<String, Any?> {
		return (obj() as Map<String, Any?>)
	}

	open fun asString(): String? {
		return obj() as String?
	}

	open fun find(tree: Array<String>): IrisJsonItem {
		var cur = this
		for (t in tree)
			cur = cur[t]
		return cur
	}

	open fun find(tree: List<String>): IrisJsonItem {
		var cur = this
		for (t in tree)
			cur = cur[t]
		return cur
	}

	open fun find(tree: String): IrisJsonItem {
		return find(tree.replace('[', '.').replace("]", "", true).replace(' ', '.').split('.'))
	}
}