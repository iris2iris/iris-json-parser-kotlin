package iris.json

import org.json.JSONObject
import java.io.File
import java.lang.StringBuilder

fun main() {

	val testString = File("test.json").readText()

	// basic start
	// Demonstration of functional abilities
	val parser = IrisJsonParser(testString)
	val res = parser.parse() // parsed to IrisJsonItem's

	// stringifies objects
	println("IrisJsonItem.toString/JSON string: " + res)

	// stringifies objects to Appendable buffer
	val b = StringBuilder()
	res.joinTo(b)
	println("IrisJsonItem.joinTo:   " + res)

	// Simple access to required object on objects tree
	println("IrisJsonItem toString/JSON string: " + res["object"]["message"]["attachments"][0]["wall"]["id"])

	// Converting to required types
	println("To Long: " + res["object"]["message"]["attachments"][0]["wall"]["id"].asLong())

	// Access by string path
	println("To Int: " + res.find("object message attachments 0 wall id").asInt())

	// Stylized to java properties access
	println("To Double: " + res.find("object.message.attachments[0].wall.id").asDouble())
	// basic end

	/********************************/

	// Run 100_000 iterations to parse json-string with standars org.json parser
	//testJsonParser(testString)

	// Run 100_000 iterations to parse json-string with Iris Json Parser
	//testIrisParser(testString)

}

fun testIrisParser(test: String) {
	System.gc()
	for (i in 1..1000)
		IrisJsonParser(test).parse()
	System.gc()
	val start = System.currentTimeMillis()
	var rand: IrisJsonItem
	for (i in 1..100_000) {
		rand = IrisJsonParser(test).parse()
		//val d = rand.obj(); // uncomment it if you want to test full object tree build. Speed is still 30% better than standard JSON lib
		if (rand === null) // check for not to let compiler optimize code
			print("true")
	}
	val end = System.currentTimeMillis()
	println((end - start).toString() + " ms")
}

fun testJsonParser(test: String) {
	System.gc()
	for (i in 1..1000)
		JSONObject.stringToValue(test)
	System.gc()
	val start = System.currentTimeMillis()
	var rand: Any
	for (i in 1..100_000) {
		rand = JSONObject(test)
		val d = rand as JSONObject?
		if (d === null) // check for not to let compiler optimize code
			print("true")
	}
	val end = System.currentTimeMillis()
	println((end - start).toString() + " ms")
}