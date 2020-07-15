package iris.json

import IrisJsonItem
import org.json.JSONObject
import java.io.File
import java.lang.StringBuilder

fun main() {

	val testString = File("test.json").readText()

	// Demonstration of functional abilities
	val parser = IrisJsonParser(testString)
	val res = parser.parse() // parsed to IrisObjectItem's

	// stringifies objects
	println(res)

	// stringifies objects to Appendable buffer
	val b = StringBuilder()
	res.joinTo(b)
	println(b)

	// Simple access to required object on objects tree
	println(res["object"]["message"]["attachments"][0]["wall"]["id"].obj())

	/********************************/

	// Run 1_000_000 iterations to parse json-string with standars org.json parser
	//testJsonParser(testString)

	// Run 1_000_000 iterations to parse json-string with Iris Json Parser
	// testIrisParser(testString)

}

fun testIrisParser(test: String) {
	System.gc()
	for (i in 1..1000)
		IrisJsonParser(test).parse()
	System.gc()
	val start = System.currentTimeMillis()
	var rand: IrisJsonItem
	for (i in 1..1_000_00) {
		rand = IrisJsonParser(test).parse()
		val d = rand.obj()
		if (d == 1) // check for not to let compiler optimize code
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
	for (i in 1..1_000_00) {
		rand = JSONObject(test)
		val d = rand as JSONObject?
		if (d == null) // check for not to let compiler optimize code
			print("true")
	}
	val end = System.currentTimeMillis()
	println((end - start).toString() + " ms")
}