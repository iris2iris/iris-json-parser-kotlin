package iris.json.test

import iris.json.flow.JsonFlowParser
import java.io.File

/** Created 20.09.2020 */
fun main() {
	val testString = File("test.json").readText()

	// Demonstration of functional abilities
	val obj = JsonFlowParser.start(testString) // parsed to IrisJsonItem's

	// stringifies result objects
	println("IrisJsonItem.toString/JSON string: $obj")

	// stringifies objects to Appendable buffer
	val b = StringBuilder()
	obj.appendToJsonString(b)
	println("IrisJsonItem.joinTo/JSON string:   $b")

	// Simple access to required object on objects tree
	println("IrisJsonItem toString/JSON string: " + obj["object"]["message"]["attachments"][0]["wall"]["id"])

	// Converting to required types
	println("To Long: " + obj["object"]["message"]["attachments"][0]["wall"]["id"].asLong())

	// Access by string path
	println("To Int: " + obj.find("object message attachments 0 wall id").asInt())

	// Stylized to Java/JavaScript properties access
	println("To Double: " + obj.find("object.message.attachments[0].wall.id").asDouble())

	obj["object"]["message"]["attachments"] = 12

	println(obj["object"]["message"]["attachments"].asInt())
}