package iris.json.flow

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.plain.IrisJsonNull
import iris.json.proxy.JsonProxyUtil
import iris.json.serialization.Deserializer
import iris.json.serialization.DeserializerClass
import java.util.*

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowObject(tokener: Tokener) : FlowItem(tokener), JsonObject {

	override fun get(ind: Int): JsonItem {
		return get(ind.toString())
	}

	private val entries = LinkedList<JsonEntry>()

	private var isDone = false
	private var needToParse: FlowItem? = null

	override fun get(key: String): JsonItem {
		for (e in entries) {
			if (e.first == key)
				return e.second
		}
		if (isDone) return IrisJsonNull.Null
		testNeedToParse()
		do {
			val next = parseNext() ?: break
			entries += next
			val nextVal = next.second as FlowItem
			if (next.first == key) {
				this.needToParse = nextVal
				return next.second
			}
			nextVal.parse()
		} while (true)

		isDone = true
		return IrisJsonNull.Null
	}

	private fun parseNext(): JsonEntry? {
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

		val key = tokener.readFieldName(char)
		char = tokener.nextChar()
		if (char != ':')
			throw tokener.exception("\":\" was expected")
		val value = JsonFlowParser.readItem(tokener)
		return JsonEntry(key, value)
	}

	private var obj: MutableMap<String, Any?>? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		parse()
		val res = HashMap<String, Any?>(entries.size)
		for (it in entries)
			res[it.first.toString()] = it.second.obj()
		obj = res
		return res
	}

	override fun set(key: String, value: Any?): JsonItem {
		val el = entries
		val index = el.indexOfFirst { it.first == key }
		val wrapValue = JsonProxyUtil.wrap(value)
		if (index == -1)
			el += JsonEntry(key, wrapValue)
		else
			el[index] = JsonEntry(key, wrapValue)
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
			(next.second as FlowItem).parse()
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
			buffer.append(entry.first)
			buffer.append("\": ")
			entry.second.joinTo(buffer)

		}
		buffer.append('}')
		return buffer
	}

	override fun <T : Any> asObject(info: Deserializer): T {
		parse()
		return (info as DeserializerClass).getObject(entries)
	}

	override fun isObject() = true

	override fun iterator(): Iterator<JsonEntry> {
		// TODO: Надо бы тут парсить по мере итератора, а не всё сразу
		parse()
		return Iter()
	}

	inner class Iter : Iterator<JsonEntry> {

		private val iterator = entries.iterator()

		override fun hasNext(): Boolean {
			return iterator.hasNext()
		}

		override fun next(): JsonEntry {
			return iterator.next()
		}
	}
}