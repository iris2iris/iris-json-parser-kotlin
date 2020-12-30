package iris.json.flow

import iris.json.Configuration
import iris.json.IrisJson
import java.io.*
import java.net.URL

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class JsonFlowParser(val tokener: Tokener, val configuration: Configuration) {

	companion object {

		fun start(url: URL, configuration: Configuration = Configuration.globalConfiguration) = start(url.openStream(), configuration)

		fun start(ins: InputStream, configuration: Configuration = Configuration.globalConfiguration) = readItem(reader(InputStreamReader(ins)), configuration)

		fun start(reader: Reader, configuration: Configuration = Configuration.globalConfiguration) = readItem(reader(reader), configuration)

		fun start(file: File, configuration: Configuration = Configuration.globalConfiguration) = readItem(reader(FileReader(file)), configuration)

		fun start(text: String, configuration: Configuration = Configuration.globalConfiguration) = readItem(TokenerString(text), configuration)

		private fun reader(reader: Reader) = TokenerContentBuildReader(reader)

		fun start(source: Tokener, configuration: Configuration = Configuration.globalConfiguration) = readItem(source, configuration)

		fun readItem(source: Tokener, configuration: Configuration = Configuration.globalConfiguration): FlowItem {
			return JsonFlowParser(source, configuration).readItem()
		}
	}

	fun readItem(): FlowItem {
		val source = this.tokener
		val char = source.nextChar()
		val type = when {
			char.isDigit() || char.isLetter() || char == '-' -> IrisJson.Type.Value
			char == '{' -> IrisJson.Type.Object
			char == '[' -> IrisJson.Type.Array
			char == '"' || char == '\'' -> IrisJson.Type.String
			else -> throw source.exception("Character: \"$char\"")
		}

		return when (type) {
			IrisJson.Type.Value -> {
				source.back(); FlowValue(source)
			}
			IrisJson.Type.Object -> FlowObject(this)
			IrisJson.Type.String -> FlowString(source, char)
			IrisJson.Type.Array -> FlowArray(this)
			else -> throw source.exception("$type not realised yet")
		}
	}
}