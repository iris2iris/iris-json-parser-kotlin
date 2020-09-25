package iris.json.flow.test

import iris.json.IrisJsonParser
import iris.json.flow.JsonFlowParser
import iris.json.flow.TokenerString
import org.json.JSONArray
import java.io.File
import kotlin.math.roundToInt

fun main() {

	val testString = File("test_array.json").readText()
	val repeats = 100_000

	var totalIris = 0.0
	var totalIrisOld = 0.0
	var totalJson = 0.0

	var totalIrisLast = 0.0
	var totalIrisOldLast = 0.0
	var totalJsonLast = 0.0


	repeat(repeats) {
		val json = testJsonParser(testString, 0)
		val iris = testIrisParser(testString, 0)
		val irisOld = testIrisParserOld(testString, 0)
		totalIris += iris
		totalIrisOld += irisOld
		totalJson += json

		val jsonLast = testJsonParser(testString, 49)
		val irisLast = testIrisParser(testString, 49)
		val irisOldLast = testIrisParserOld(testString, 49)
		totalIrisLast += irisLast
		totalIrisOldLast += irisOldLast
		totalJsonLast += jsonLast
	}

	totalIris /= 1000000.0
	totalIrisOld /= 1000000.0
	totalJson /= 1000000.0
	println("AVG[0]:" +
			"\norg.json:   ${totalJson.roundToInt()}" +
			"\nIris Plain: ${totalIrisOld.roundToInt()}" +
			"\nIris Flow:  ${totalIris.roundToInt()}"
	)

	println()

	totalIrisLast /= 1000000.0
	totalIrisOldLast /= 1000000.0
	totalJsonLast /= 1000000.0
	println("AVG[49]:" +
			"\norg.json:   ${totalJsonLast.roundToInt()}" +
			"\nIris Plain: ${totalIrisOldLast.roundToInt()}" +
			"\nIris Flow:  ${totalIrisLast.roundToInt()}"
	)
}

fun testIrisParserOld(test: String, ind: Int): Long {
	val start = System.nanoTime()
	val rand = IrisJsonParser(test).parse()
	val d = rand[ind]["ID"].asString()
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}

fun testIrisParser(test: String, ind: Int): Long {
	val start = System.nanoTime()
	val rand = JsonFlowParser.readItem(TokenerString(test))
	val d = rand[ind]["ID"].asString()
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}

fun testJsonParser(test: String, ind: Int): Long {

	val start = System.nanoTime()
	val rand = JSONArray(test)
	val d = rand.getJSONObject(ind).getString("ID")
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}