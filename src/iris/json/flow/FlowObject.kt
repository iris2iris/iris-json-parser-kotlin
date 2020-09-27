package iris.json.flow

import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.JsonObject.Entry
import iris.json.plain.IrisJsonNull
import iris.json.proxy.JsonProxyUtil

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowObject(tokener: Tokener) : FlowItem(tokener), JsonObject {

	/*private class Entry(val key: CharSequence, val value: JsonItem) {
		override fun toString(): String {
			return "\"$key\": $value"
		}
	}*/

	override fun get(ind: Int): JsonItem {
		return get(ind.toString())
	}

	private val entries = mutableListOf<Entry>()

	private var isDone = false
	private var needToParse: FlowItem? = null

	override fun get(key: String): JsonItem {
		for (e in entries) {
			if (e.key == key)
				return e.value
		}
		if (isDone) return IrisJsonNull.Null
		testNeedToParse()
		do {
			val next = parseNext() ?: break
			entries += next
			val nextVal = next.value as FlowItem
			if (next.key == key) {
				this.needToParse = nextVal
				return next.value
			}
			nextVal.parse()
		} while (true)

		isDone = true
		return IrisJsonNull.Null
	}

	private fun parseNext(): Entry? {
		var char = tokener.nextChar()
		if (char == '}') {
			isDone = true
			return null
		}
		if (char == ',') {
			char = tokener.nextChar()
		}
		if (!(char == '"' || char == '\''))
			throw tokener.exception("\" (quote) or \"'\" was expected")

		val key = tokener.readString(char)
		char = tokener.nextChar()
		if (char != ':')
			throw tokener.exception("\":\" was expected")
		val value = JsonFlowParser.readItem(tokener)
		return Entry(key, value)
	}

	private var obj: MutableMap<String, Any?>? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		parse()
		val res = mutableMapOf<String, Any?>()
		for (it in entries)
			res[it.key.toString()] = it.value.obj()
		obj = res
		return res
	}

	override fun set(key: String, value: Any?): JsonItem {
		val el = entries
		val index = el.indexOfFirst { it.key == key }
		val wrapValue = JsonProxyUtil.wrap(value)
		if (index == -1)
			el += Entry(key, wrapValue)
		else
			el[index] = Entry(key, wrapValue)
		val obj = obj
		if (obj != null) {
			obj[key] = value
		}
		return this
	}

	override fun set(ind: Int, value: Any?): JsonItem {
		return set(ind.toString(), value)
	}

	override fun parse() {
		if (isDone) return
		testNeedToParse()
		do {
			val next = parseNext() ?: break
			entries += next
			(next.value as FlowItem).parse()
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

	override fun <A : Appendable> joinTo(buffer: A): A {
		parse()
		buffer.append("{")
		var firstDone = false
		for (entry in entries) {
			if (firstDone)
				buffer.append(", ")
			else
				firstDone = true
			buffer.append("\"")
			buffer.append(entry.key)
			buffer.append("\": ")
			entry.value.joinTo(buffer)

		}
		buffer.append('}')
		return buffer
	}

	override fun isObject() = true

	override fun iterator(): Iterator<Entry> {
		// TODO: Надо бы тут парсить по мере итератора, а не всё сразу
		parse()
		return Iter()
	}

	inner class Iter : Iterator<Entry> {

		private val iterator = entries.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): Entry {
			val e = iterator.next()
			return Entry(e.key, e.value)
		}
	}
}