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

        if (type == IrisJson.Type.Value) { // primitives
            source.back()
            return FlowValue(source)
        } else if (type == IrisJson.Type.Object) {
            return FlowObject(source)
        } else if (type == IrisJson.Type.String) {
            return FlowString(source, char)
        } else if (type == IrisJson.Type.Array) {
            return FlowArray(source)
        } else
            throw source.exception("$type not realised yet")
    }
}