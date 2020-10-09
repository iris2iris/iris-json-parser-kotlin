package iris.json.serialization

import iris.json.JsonEntry

interface DeserializerClass : Deserializer {
	fun <T: Any>getObject(entries: List<JsonEntry>): T
}