package iris.json.test

import iris.json.plain.IrisJsonItem
import iris.json.plain.IrisJsonParser
import org.json.JSONObject
import java.io.File

fun main() {

	val testString = File("test.json").readText()

	/********************************/

	// Run 100_000 iterations to parse json-string with standars org.json parser
	testJsonParser(testString)

	// Run 100_000 iterations to parse json-string with Iris Json Parser
	//testIrisParser(testString)

}

fun testIrisParser(test: String) {
	for (i in 1..1000)
		IrisJsonParser(test).parse()

	val start = System.currentTimeMillis()
	var rand: IrisJsonItem
	for (i in 1..100_000) {
		rand = IrisJsonParser(test).parse()
		//val d = rand.obj(); // uncomment it if you want to test full object tree build. Speed is still 30% better than standard JSON lib
		if (rand.hashCode() == 12) // check for not to let compiler optimize code
			print("true")
	}
	val end = System.currentTimeMillis()
	println((end - start).toString() + " ms")
}

fun testJsonParser(test: String) {
	for (i in 1..1000)
		JSONObject.stringToValue(test)

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