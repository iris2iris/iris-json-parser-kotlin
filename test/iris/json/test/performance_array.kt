package iris.json.test

import iris.json.flow.JsonFlowParser
import iris.json.flow.TokenerString
import iris.json.plain.JsonPlainParser
import iris.json.proxy.JsonProxyArray
import org.json.JSONArray
import org.json.simple.parser.JSONParser
import java.io.File
import kotlin.math.roundToInt

fun main() {

	val testString = File("test_array.json").readText()
	val repeats = 100_000

	var totalIrisFlow = 0.0
	var totalIrisProxy = 0.0
	var totalIrisPlain = 0.0
	var totalJson = 0.0
	var totalSimpleJson = 0.0
	var totalPlainAccess = 0.0

	var totalIrisFlowLast = 0.0
	var totalIrisProxyLast = 0.0
	var totalIrisPlainLast = 0.0
	var totalJsonLast = 0.0
	var totalSimpleJsonLast = 0.0
	var totalPlainAccessLast = 0.0
	val map = JsonPlainParser(testString).parse().asList()

	// little warmup
	testJsonParser(testString, 0)
	testSimpleJsonParser(testString, 0)
	testIrisParserFlow(testString, 0)
	testIrisProxy(map, 0)
	testIrisParserPlain(testString, 0)
	testPlainAccess(map, 0)

	testJsonParser(testString, 49)
	testSimpleJsonParser(testString, 49)
	testIrisParserFlow(testString, 49)
	testIrisProxy(map, 49)
	testIrisParserPlain(testString, 49)
	testPlainAccess(map, 49)

	repeat(repeats) {
		//totalJson += testJsonParser(testString, 0)
		//totalSimpleJson += testSimpleJsonParser(testString, 0)
		totalIrisFlow += testIrisParserFlow(testString, 0)
		totalIrisPlain += testIrisParserPlain(testString, 0)
		totalIrisProxy += testIrisProxy(map, 0)
		totalPlainAccess += testPlainAccess(map, 0)

		//totalJsonLast += testJsonParser(testString, 49)
		//totalSimpleJsonLast += testSimpleJsonParser(testString, 49)
		totalIrisFlowLast += testIrisParserFlow(testString, 49)
		totalIrisPlainLast += testIrisParserPlain(testString, 49)
		totalIrisProxyLast += testIrisProxy(map, 49)
		totalPlainAccessLast += testPlainAccess(map, 49)
		if (it != 0 && it % 10_000 == 0)
			println("$it iteration")
	}

	totalIrisFlow /= 1000000.0
	totalIrisProxy /= 1000000.0
	totalIrisPlain /= 1000000.0
	totalJson /= 1000000.0
	totalSimpleJson /= 1000000.0
	totalPlainAccess /= 1000000.0
	println("AVG[0]:" +
			"\norg.json:   ${totalJson.roundToInt()}" +
			"\norg.json.simple: ${totalSimpleJson.roundToInt()}" +
			"\nIris Plain: ${totalIrisPlain.roundToInt()}" +
			"\nIris Flow:  ${totalIrisFlow.roundToInt()}" +
			"\nIris Proxy: ${totalIrisProxy.roundToInt()}" +
			"\nPOJO:       ${totalPlainAccess.roundToInt()}"
	)

	println()

	totalIrisFlowLast /= 1000000.0
	totalIrisPlainLast /= 1000000.0
	totalJsonLast /= 1000000.0
	totalSimpleJsonLast /= 1000000.0
	totalIrisProxyLast /= 1000000.0
	totalPlainAccessLast /= 1000000.0
	println("AVG[49]:" +
			"\norg.json:   ${totalJsonLast.roundToInt()}" +
			"\norg.json.simple: ${totalSimpleJsonLast.roundToInt()}" +
			"\nIris Plain: ${totalIrisPlainLast.roundToInt()}" +
			"\nIris Flow:  ${totalIrisFlowLast.roundToInt()}" +
			"\nIris Proxy: ${totalIrisProxyLast.roundToInt()}" +
			"\nPOJO:       ${totalPlainAccessLast.roundToInt()}"
	)
}

fun testPlainAccess(map: List<Any?>, ind: Int): Long {
	val start = System.nanoTime()
	val d = (map[ind] as Map<String, Any?>)["ID"] as String
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}

fun testIrisProxy(map: List<Any?>, ind: Int): Long {
	val start = System.nanoTime()
	val rand = JsonProxyArray(map)
	val d = rand[ind]["ID"].asString()
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}

fun testIrisParserPlain(test: String, ind: Int): Long {
	val start = System.nanoTime()
	val rand = JsonPlainParser(test).parse()
	val d = rand[ind]["ID"].asString()
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}

fun testIrisParserFlow(test: String, ind: Int): Long {
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
fun testSimpleJsonParser(test: String, ind: Int): Long {
	val start = System.nanoTime()
	val rand = JSONParser().parse(test)
	val d = ((rand as List<Any?>)[ind] as Map<String, Any?>)["ID"]
	if (d == "1") // check for not to let compiler optimize code
		print("true")
	val end = System.nanoTime()
	return end - start
}