package iris.json.plain

import iris.json.IrisJson
import iris.json.JsonObject.Entry
import iris.sequence.IrisSubSequence
import kotlin.math.max
import kotlin.math.min

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonParser(private val source: String) {

	private var pointer = 0

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
			return IrisJsonValue(IrisSubSequence(source, start, end), value)
		} else if (type == IrisJson.Type.Object) {
			return readObject()
		} else if (type == IrisJson.Type.String) {
			val start = pointer
			readString(char)
			val end = pointer - 1
			//counter++ // поправка, т.к. мы вышли из строки, узнав про кавычку. на неё и двигаемся
			return IrisJsonString(IrisSubSequence(source, start, end))
		} else if (type == IrisJson.Type.Array) {
			return readArray()
		} else
			TODO("$type not realised yet $pointer\n" + getPlace())
	}

	private fun getPlace(): String {
		return '"' + source.substring(max(0, pointer - 10), min(pointer + 10, source.length - 1)) + '"'
	}

	private fun readObject(): IrisJsonObject {
		val entries = mutableListOf<Entry>()
		val len = source.length
		do {
			skipWhitespaces()
			// "id" : ...
			var char = source[pointer++]
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
			val key = IrisSubSequence(source, start, end)
			skipWhitespaces()
			char = source[pointer++]
			if (char != ':')
				throw IllegalArgumentException("\":\" was expected in position $pointer\n" + getPlace())
			skipWhitespaces()
			val value = readItem()
			entries.add(Entry(key, value))
			//counter--
		} while (pointer < len)
		return IrisJsonObject(entries)
	}

	private fun readArray(): IrisJsonArray {
		val entries = mutableListOf<IrisJsonItem>()
		val len = source.length
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
		val len = source.length
		do {
			val char = source[pointer]
			if (!char.isWhitespace()) {
				break
			}
			pointer++
		} while (pointer < len)
	}

	private fun readString(quote: Char) {
		var escaping = false
		val len = source.length
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
		val len = source.length
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