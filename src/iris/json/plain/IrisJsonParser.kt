package iris.json.plain

import iris.json.IrisJson
import iris.json.JsonEntry
import iris.json.flow.TokenerString
import iris.sequence.IrisSequenceCharArray
import java.util.*
import kotlin.collections.ArrayList

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonParser(source: String) {

	private var pointer = 0
	private val source: CharArray = source.toCharArray()


	fun parse(): IrisJsonItem {
		return readItem()
	}

	private fun readItem(): IrisJsonItem {
		skipWhitespaces()
		val char = source[pointer++]
		val type = when {
			char.isDigit() || char.isLetter() || char == '-' -> IrisJson.Type.Value
			char == '{' -> IrisJson.Type.Object
			char == '[' -> IrisJson.Type.Array
			char == '"' || char == '\'' -> IrisJson.Type.String
			else -> throw IllegalArgumentException("Character: \"$char\" at $pointer\n" + getPlace())
		}

		if (type == IrisJson.Type.Value) { // примитивы
			pointer--
			val start = pointer
			val value = readPrimitive()
			val end = pointer
			return IrisJsonValue(IrisSequenceCharArray(source, start, end), value)
		} else if (type == IrisJson.Type.Object) {
			return readObject()
		} else if (type == IrisJson.Type.String) {
			val start = pointer
			readString(char)
			val end = pointer - 1
			return IrisJsonString(IrisSequenceCharArray(source, start, end))
		} else if (type == IrisJson.Type.Array) {
			return readArray()
		} else
			TODO("$type not realised yet $pointer\n" + getPlace())
	}

	private fun getPlace(): String {
		return ""
		//return '"' + source.substring(max(0, pointer - 10), min(pointer + 10, source.length - 1)) + '"'
	}

	private fun readObject(): IrisJsonObject {

		//val key = readKey() ?: return IrisJsonObject(ArrayList(0))
		val key = readKey() ?: return IrisJsonObject(ArrayList(0))
		val value = readValue()

		val key2 = readKey() ?: return IrisJsonObject(ArrayList<JsonEntry>(1).also { it += key to value })
		val value2 = readValue()

		val key3 = readKey() ?: return IrisJsonObject(ArrayList<JsonEntry>(2).also { it += key to value; it += key2 to value2 })
		val value3 = readValue()

		val entries = LinkedList<JsonEntry>()
		entries += key to value
		entries += key2 to value2
		entries += key3 to value3

		val len = source.size
		var char: Char
		do {
			skipWhitespaces()
			// "id" : ...
			char = source[pointer++]
			if (char == '}')
				break
			if (char == ',') {
				skipWhitespaces()
				char = source[pointer++]
			}
			if (!(char == '"' || char == '\''))
				throw IllegalArgumentException("\" (quote) or \"'\" was expected in position $pointer\n" + getPlace())

			val start = pointer
			// ключ
			readString(char)
			val end = pointer - 1
			val key = IrisSequenceCharArray(source, start, end)
			skipWhitespaces()
			char = source[pointer++]
			if (char != ':')
				throw IllegalArgumentException("\":\" was expected in position $pointer\n" + getPlace())
			skipWhitespaces()
			val value = readItem()
			entries += key to value
			//counter--
		} while (pointer < len)
		return IrisJsonObject(entries)
	}

	private fun readValue(): IrisJsonItem {
		skipWhitespaces()
		val char = source[pointer++]
		if (char != ':')
			throw IllegalArgumentException("\":\" was expected in position $pointer\n" + getPlace())
		skipWhitespaces()
		return readItem()
	}

	private fun readKey(): CharSequence? {
		skipWhitespaces()
		// "id" : ...
		var char = source[pointer++]
		if (char == '}')
			return null
		if (char == ',') {
			skipWhitespaces()
			char = source[pointer++]
		}
		if (!(char == '"' || char == '\''))
			throw IllegalArgumentException("\" (quote) or \"'\" was expected in position $pointer\n" + getPlace())

		val start = pointer
		// ключ
		readString(char)
		val end = pointer - 1
		return IrisSequenceCharArray(source, start, end)
		//return String(source, start, end - start)
	}

	private fun readArray(): IrisJsonArray {
		val entries = mutableListOf<IrisJsonItem>()
		val len = source.size
		do {
			skipWhitespaces()
			var char = source[pointer++]
			if (char == ']')
				break
			if (char == ',') {
				skipWhitespaces()
			} else
				pointer--

			val value = readItem()
			entries.add(value)
			skipWhitespaces()
			char = source[pointer]
			if (char == ']') {
				pointer++
				break
			}
		} while (pointer < len)
		return IrisJsonArray(entries)
	}

	private fun skipWhitespaces() {
		val len = source.size
		do {
			val char = source[pointer].toInt()
			if (char > TokenerString.SPACE)
				break
			if (!(char == TokenerString.SPACE || char == TokenerString.TAB || char == TokenerString.LF || char == TokenerString.CR))
				break
			pointer++
		} while (pointer < len)
	}

	private fun readString(quote: Char) {
		var escaping = false
		val len = source.size
		do {
			val char = source[pointer++]
			if (char == '\\')
				escaping = true
			else if (escaping) {
				escaping = false
			} else if (char == quote) {
				break
			}
		} while (pointer < len)
	}

	private fun readPrimitive(): IrisJson.ValueType {
		var curType = IrisJson.ValueType.Integer
		val first = pointer
		val len = source.size
		loop@ do {
			val char = source[pointer]
			when {
				char.isDigit() -> {}
				char == '-' -> if (first != pointer) curType = IrisJson.ValueType.Constant
				char == '.' -> if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				char.isLetter() -> curType = IrisJson.ValueType.Constant
				else -> break@loop
			}
			pointer++
		} while (pointer < len)
		return curType
	}
}