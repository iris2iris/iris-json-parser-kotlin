package iris.json.flow

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import iris.json.plain.IrisJsonNull
import java.util.*

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowObject(private val parser: JsonFlowParser) : FlowItem(parser.tokener), JsonObject {

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
		val tokener = this.tokener
		var char: Char? = tokener.nextChar()
		if (char == '}') {
			isDone = true
			return null
		}
		if (char == ',') {
			char = tokener.nextChar()
		}
		if (!(char == '"' || char == '\'')) {
			if (char in 'a'..'z' || char in 'A'..'Z') {
				char = null
				tokener.back()
			} else
				throw tokener.exception("\" (quote) or \"'\" was expected")
		}


		val key = tokener.readFieldName(char)
		char = tokener.nextChar()
		if (char != ':')
			throw tokener.exception("\":\" was expected")
		val value = parser.readItem()
		return JsonEntry(key, value)
	}

	private var obj: MutableMap<String, Any?>? = null

	override fun obj(): Any? {
		if (obj != null)
			return obj
		parse()
		val res = parser.configuration.mapObjectFactory.getMap(entries.size)

		for (it in entries)
			res[it.first.toString()] = it.second.obj()
		obj = res
		return res
	}

	override fun set(key: String, value: JsonItem): JsonItem {
		val el = entries
		val index = el.indexOfFirst { it.first == key }
		if (index == -1)
			el += JsonEntry(key, value)
		else
			el[index] = JsonEntry(key, value)
		val obj = obj
		if (obj != null) {
			obj[key] = value.obj()
		}
		return this
	}

	override fun set(ind: Int, value: JsonItem): JsonItem {
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

	override fun getEntries(): Collection<JsonEntry> {
		parse()
		return entries
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