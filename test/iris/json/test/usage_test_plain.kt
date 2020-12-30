package iris.json.test

import iris.json.plain.JsonPlainParser
import java.io.File

/**
 * @created 06.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 *
 */

fun main() {
	val testString = File("test.json").readText()

	// basic start
	// Demonstration of functional abilities
	val parser = JsonPlainParser(testString)
	val res = parser.parse() // parsed to IrisJsonItem's

	// stringifies result objects
	println("IrisJsonItem.toString/JSON string: $res")

	// stringifies objects to Appendable buffer
	val b = StringBuilder()
	res.joinTo(b)
	println("IrisJsonItem.joinTo/JSON string:   $b")

	// Simple access to required object on objects tree
	println("IrisJsonItem toString/JSON string: " + res["object"]["message"]["attachments"][0]["wall"]["id"])

	// Converting to required types
	println("To Long: " + res["object"]["message"]["attachments"][0]["wall"]["id"].asLong())

	// Access by string path
	println("To Int: " + res.find("object message attachments 0 wall id").asInt())

	// Stylized to Java/JavaScript properties access
	println("To Double: " + res.find("object.message.attachments[0].wall.id").asDouble())
	// basic end
}