package iris.json.flow

import iris.json.JsonArray
import iris.json.JsonItem

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowArray(private val parser: JsonFlowParser) : FlowItem(parser.tokener), JsonArray {

	private val items = mutableListOf<JsonItem>()

	override fun <A : Appendable> appendToJsonString(buffer: A): A {
		parse()
		buffer.append('[')
		if (items.isNotEmpty()) {
			val first = items.first()
			items.forEach {
				if (first !== it)
					buffer.append(", ")
				it.appendToJsonString(buffer)
			}
		}
		buffer.append(']')
		return buffer
	}

	private var isDone = false
	private var needToParse: FlowItem? = null

	override fun get(ind: Int): JsonItem {
		if (isDone)
			return items[ind]
		val toAdd = ind - items.size
		if (toAdd < 0)
			return items[ind]

		testNeedToParse()
		if (toAdd > 0) {
			for (i in 0 until toAdd) {
				val next = parseNext()
				if (next == null) {
					isDone = true
					break
				}
				items += next
				next.parse()
			}
		}
		val next = parseNext()
		if (next == null) {
			isDone = true
		} else {
			items += next
			needToParse = next
		}

		return items[ind]
	}

	private fun parseNext(): FlowItem? {
		val char = tokener.nextChar()
		if (char == ']') {
			isDone = true
			return null
		}
		if (char != ',')
			tokener.back()
		else {
			val char = tokener.nextChar()
			if (char == ']') {
				if (!parser.configuration.trailingCommaAllowed)
					throw tokener.exception("Trailing commas are not allowed in current configuration settings. ")
				return null
			}
			tokener.back()
		}

		return parser.readItem()
	}

	private fun parseNextAndAdd(): FlowItem? {
		val next = parseNext()
		if (next == null) {
			isDone = true
			return null
		}
		items += next
		next.parse()
		return next
	}

	override fun parse() {
		if (isDone)
			return
		testNeedToParse()
		do {
			val next = parseNext() ?: break
			next.parse()
			items += next
		} while (true)
		isDone = true
	}

	private fun testNeedToParse() {
		val needToParse = this.needToParse
		if (needToParse != null) {
			needToParse.parse()
			this.needToParse = null
		}
	}

	override fun get(key: String): JsonItem {
		val ind = key.toInt()
		return get(ind)
	}

	override fun getList(): List<JsonItem> {
		parse()
		return items
	}

	override val size: Int
		get() = getList().size

	override fun isEmpty(): Boolean {
		if (isDone) return items.isEmpty()
		if (items.isNotEmpty()) return false
		val res = parseNextAndAdd()
		return res == null
	}

	override fun isNotEmpty(): Boolean {
		if (isDone) return items.isNotEmpty()
		if (items.isNotEmpty()) return true
		val res = parseNextAndAdd()
		return res != null
	}

	override fun set(ind: Int, value: JsonItem): JsonItem {
		items[ind] = value
		val obj = this.obj
		if (obj != null)
			obj[ind] = value.obj()
		return this
	}

	override fun set(key: String, value: JsonItem): JsonItem {
		return set(key.toInt(), value)
	}

	private var obj : MutableList<Any?>? = null

	override fun obj(): Any? {
		return obj ?: run {
			parse()
			obj = items.mapTo(ArrayList(items.size)) { it.obj() }
			obj
		}
	}

	override fun iterable() = this

	override fun iterator(): Iterator<JsonItem> {
		return Iter()
	}

	override fun isArray() = true

	private inner class Iter : Iterator<JsonItem> {

		private var pointer = 0

		override fun hasNext(): Boolean {
			if (isDone) {
				return pointer < items.size
			}

			while (pointer >= items.size) {
				parseNextAndAdd() ?: break
			}
			return pointer < items.size
		}

		override fun next(): JsonItem {
			return get(pointer++)
		}
	}
}