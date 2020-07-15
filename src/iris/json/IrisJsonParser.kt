package iris.json

import IrisJsonItem

class IrisJsonParser(private val array: String) {

	private var counter = 0

	fun parse(): IrisJsonItem {
		return readItem()
	}

	private fun readItem(): IrisJsonItem {
		skipWhitespaces()
		val char = array[counter++]
		val type = when {
			char.isDigit() || char.isLetter() || char == '-' -> IrisJson.Type.Value
			char == '{' -> IrisJson.Type.Object
			char == '[' -> IrisJson.Type.Array
			char == '"' -> IrisJson.Type.String
			else -> throw IllegalArgumentException("Character: \"$char\" at $counter\n" + getPlace())
		}

		if (type == IrisJson.Type.Value) { // примитивы
			counter--
			val start = counter
			val value = readPrimitive()
			val end = counter
			return IrisJsonValue(IrisSequence(array, start, end), value)
		} else if (type == IrisJson.Type.Object) {
			return readObject()
		} else if (type == IrisJson.Type.String) {
			val start = counter
			readString()
			val end = counter - 1
			counter++ // поправка, т.к. мы вышли из строки, узнав про кавычку. на неё и двигаемся
			return IrisJsonString(IrisSequence(array, start, end))
		} else if (type == IrisJson.Type.Array) {
			return readArray()
		} else
			TODO("$type not realised yet $counter\n" + getPlace())
	}

	private fun getPlace(): String {
		return '"' + array.substring(Math.max(0, counter - 10), Math.min(counter + 10, array.length - 1))+'"'
	}

	private fun readObject(): IrisJsonObject {
		val entries = mutableListOf<IrisJsonObject.Entry>()
		do {
			skipWhitespaces()
			// "id" : ...
			var char = array[counter++]
			if (char == '}')
				break
			if (char == ',') {
				skipWhitespaces()
				char = array[counter++]
			}
			if (char != '"')
				throw IllegalArgumentException("\" (quote) was expected in position $counter\n" + getPlace())

			val start = counter
			// ключ
			readString()
			val end = counter - 1
			val key = IrisSequence(array, start, end)
			skipWhitespaces()
			char = array[counter++]
			if (char != ':')
				throw IllegalArgumentException("\":\" was expected in position $counter\n" + getPlace())
			skipWhitespaces()
			val value = readItem()
			entries.add(IrisJsonObject.Entry(key, value))
			//counter--
		} while (counter < array.length)
		return IrisJsonObject(entries)
	}

	private fun readArray(): IrisJsonArray {
		val entries = mutableListOf<IrisJsonItem>()
		do {
			skipWhitespaces()
			// "id" : ...
			var char = array[counter++]
			if (char == ']')
				break
			if (char == ',') {
				skipWhitespaces()
			} else
				counter--

			val value = readItem()
			entries.add(value)
			skipWhitespaces()
			char = array[counter]
			if (char == ']') {
				counter++
				break
			}
		} while (counter < array.length)
		return IrisJsonArray(entries)
	}

	private fun skipWhitespaces() {
		do {
			val char = array[counter]
			if (!char.isWhitespace()) {
				//counter--
				break
			}
			counter++
		} while (counter < array.length)
	}

	private fun readString() {
		var escaping = false
		do {
			val char = array[counter++]
			if (char == '\\')
				escaping = true
			else if (escaping) {
				escaping = false
			} else if (char == '"') {
				break
			}
		} while (counter < array.length)
	}

	private fun readPrimitive(): IrisJson.ValueType {
		var curType = IrisJson.ValueType.Integer
		val first = counter
		loop@ do {
			val char = array[counter]
			when {
				char.isDigit() -> {
				}
				char == '-' -> {
					if (first != counter) curType = IrisJson.ValueType.Constant
				}
				char == '.' -> {
					if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				}
				char.isLetter() -> {
					curType = IrisJson.ValueType.Constant
				}
				else -> break@loop
			}
			counter++
		} while (counter < array.length)
		return curType
	}
}