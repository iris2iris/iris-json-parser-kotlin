package iris.json.plain

import iris.json.Configuration
import iris.json.IrisJson
import iris.json.JsonEntry
import iris.json.Util
import iris.json.flow.TokenerString
import iris.sequence.IrisSequenceCharArray
import java.io.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonPlainParser(source: String, private val configuration: Configuration = Configuration.globalConfiguration) {

	companion object {

		fun parse(url: URL, configuration: Configuration = Configuration.globalConfiguration) =
			parse(url.openStream(), configuration)

		fun parse(ins: InputStream, configuration: Configuration = Configuration.globalConfiguration) =
			parse(reader(InputStreamReader(ins)), configuration)

		fun parse(reader: Reader, configuration: Configuration = Configuration.globalConfiguration) =
			parse(reader(reader), configuration)

		fun parse(file: File, configuration: Configuration = Configuration.globalConfiguration) =
			parse(reader(FileReader(file)), configuration)

		private fun reader(reader: Reader) = reader.use { it.readText() }

		fun parse(source: String, configuration: Configuration = Configuration.globalConfiguration): IrisJsonItem {
			return JsonPlainParser(source, configuration).readItem()
		}
	}

	private var pointer = 0
	private val source: CharArray = source.toCharArray()

	fun parse(): IrisJsonItem {
		return readItem()
	}

	private fun readItem(): IrisJsonItem {
		skipWhitespaces()
		val char = source[pointer++]
		val type = when {
			Util.isDigitOrAlpha(char) || char == '-' -> IrisJson.Type.Value
			char == '{' -> IrisJson.Type.Object
			char == '[' -> IrisJson.Type.Array
			char == '"' || char == '\'' -> IrisJson.Type.String
			else -> throw parseException("Unexpected character \"$char\" in object type detection state")
		}

		when (type) {
			IrisJson.Type.Value -> { // примитивы
				pointer--
				val start = pointer
				val value = readPrimitive()
				val end = pointer
				return IrisJsonValue(IrisSequenceCharArray(source, start, end), value)
			}
			IrisJson.Type.Object -> {
				return readObject()
			}
			IrisJson.Type.String -> {
				val start = pointer
				readString(char)
				val end = pointer - 1
				return IrisJsonString(IrisSequenceCharArray(source, start, end))
			}
			IrisJson.Type.Array -> {
				return readArray()
			}
			else -> TODO("$type not realised yet $pointer\n" + getPlace())
		}
	}

	private fun getPlace(): String {
		return '"' + String(source, max(0, pointer - 10), min(pointer + 10, source.size - 1)) + '"'
	}

	private fun readObject(): IrisJsonObject {

		val key = readKey() ?: return IrisJsonObject(ArrayList(0), configuration)
		val value = readValue()

		val key2 = readKey() ?: return IrisJsonObject(ArrayList<JsonEntry>(1).also { it += key to value }, configuration)
		val value2 = readValue()

		val key3 = readKey() ?: return IrisJsonObject(ArrayList<JsonEntry>(2).also { it += key to value; it += key2 to value2 }, configuration)
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
		return IrisJsonObject(entries, configuration)
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
		if (!(char == '"' || char == '\'')) {
			if (char in 'a'..'z' || char in 'A'..'Z') {
				val start = pointer - 1
				// ключ
				readPrimitive()
				val end = pointer
				return IrisSequenceCharArray(source, start, end)
			} else
				throw IllegalArgumentException("\" (quote) or \"'\" was expected in position $pointer\n" + getPlace())
		} else {
			val start = pointer
			// ключ
			readString(char)
			val end = pointer - 1
			return IrisSequenceCharArray(source, start, end)
		}
	}

	private fun readArray(): IrisJsonArray {

		skipWhitespaces()
		var char = source[pointer]
		if (char == ']') {
			pointer++
			return IrisJsonArray(emptyList())
		}

		val entries = mutableListOf<IrisJsonItem>()
		val len = source.size
		do {

			val value = readItem()
			entries += value

			skipWhitespaces()
			char = source[pointer]
			if (char == ']') {
				pointer++
				break
			}
			if (char != ',') {
				throw parseException(""""," was excepted, but "$char" found""")
			}
			pointer++
			skipWhitespaces()

			char = source[pointer]
			if (char == ']') {
				if (!configuration.trailingCommaAllowed)
					throw parseException("Trailing commas are not allowed in current configuration settings. ")
				pointer++
				break
			}

			/*if (char == ',') {
				pointer++
				skipWhitespaces()
			}*//* else
				pointer--*/



			/*skipWhitespaces()
			val char = source[pointer]
			if (char == ']') {
				pointer++
				break
			}
			if (char == ',') {
				pointer++
				skipWhitespaces()
			}*//* else
				pointer--*/

			/*val value = readItem()
			entries.add(value)*/


			//skipWhitespaces()
			/*char = source[pointer]
			if (char == ']') {
				pointer++
				break
			}*/
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
		var char: Char
		do {
			char = source[pointer++]
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
		do {
			val char = source[pointer]
			when {
				Util.isDigit(char) -> {}
				char == '-' -> if (first != pointer) curType = IrisJson.ValueType.Constant
				char == '.' -> if (curType == IrisJson.ValueType.Integer) curType = IrisJson.ValueType.Float
				Util.isAlpha(char) -> curType = IrisJson.ValueType.Constant
				else -> break
			}
			pointer++
		} while (pointer < len)
		return curType
	}

	fun parseException(message: String): IllegalStateException {
		return IllegalStateException("$message\nAt position $pointer: " + String(source.copyOfRange(max(0, pointer - 10), min(pointer + 10, source.size - 1))))
	}
}