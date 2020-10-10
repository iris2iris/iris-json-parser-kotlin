package iris.json.flow

import iris.json.IrisJson
import java.io.*
import java.net.URL

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
object JsonFlowParser {

	fun start(url: URL) = start(url.openStream())

	fun start(ins: InputStream) = readItem(reader(InputStreamReader(ins)))

	fun start(reader: Reader) = readItem(reader(reader))

	fun start(file: File) = readItem(reader(FileReader(file)))

	fun start(text: String) = readItem(TokenerString(text))

	private fun reader(reader: Reader) = TokenerContentBuildReader(reader)

	fun start(source: Tokener) = readItem(source)

	fun readItem(source: Tokener): FlowItem {
		val char = source.nextChar()
		val type = when {
			char.isDigit() || char.isLetter() || char == '-' -> IrisJson.Type.Value
			char == '{' -> IrisJson.Type.Object
			char == '[' -> IrisJson.Type.Array
			char == '"' || char == '\'' -> IrisJson.Type.String
			else -> throw source.exception("Character: \"$char\"")
		}

		return when (type) {
			IrisJson.Type.Value -> { source.back(); FlowValue(source) }
			IrisJson.Type.Object -> FlowObject(source)
			IrisJson.Type.String -> FlowString(source, char)
			IrisJson.Type.Array -> FlowArray(source)
			else -> throw source.exception("$type not realised yet")
		}
	}
}