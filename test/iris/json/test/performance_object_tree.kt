package iris.json.test

import iris.json.flow.JsonFlowParser
import iris.json.flow.TokenerString
import iris.json.plain.JsonPlainParser
import iris.json.proxy.JsonProxyObject
import org.json.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import kotlin.math.roundToInt

fun main() {
	PerformanceObjectTree.main()
}

object PerformanceObjectTree {
	fun main() {
		val testString = File("test.json").readText()

		var totalIris = 0.0
		var totalIrisProxy = 0.0
		var totalIrisOld = 0.0
		var totalJson = 0.0
		var totalSimpleJson = 0.0
		var totalPlainAccess = 0.0

		val map = JsonPlainParser(testString).parse().asMap()

		// little warmup
		testJsonParser(testString)
		testSimpleJsonParser(testString)
		testIrisParser(testString)
		testIrisProxy(map)
		testIrisParserOld(testString)
		testPlainAccess(map)


		repeat(100_000) {
			//totalJson += testJsonParser(testString)
			//totalSimpleJson += testSimpleJsonParser(testString)
			totalIris += testIrisParser(testString)
			totalIrisOld += testIrisParserOld(testString)
			totalIrisProxy += testIrisProxy(map)
			totalPlainAccess += testPlainAccess(map)

			if (it != 0 && it % 10_000 == 0)
				println("$it iteration")
		}

		totalIris /= 1000000.0
		totalIrisProxy /= 1000000.0
		totalIrisOld /= 1000000.0
		totalJson /= 1000000.0
		totalSimpleJson /= 1000000.0
		totalPlainAccess /= 1000000.0
		println("AVG:" +
				"\norg.json:   ${totalJson.roundToInt()}" +
				"\norg.json.simple: ${totalSimpleJson.roundToInt()}" +
				"\nIris Plain: ${totalIrisOld.roundToInt()}" +
				"\nIris Flow:  ${totalIris.roundToInt()}" +
				"\nIris Proxy: ${totalIrisProxy.roundToInt()}" +
				"\nPOJO:       ${totalPlainAccess.roundToInt()}"
		)
	}

	fun testPlainAccess(map: Map<*, *>): Long {
		val start = System.nanoTime()
		val d = map.asMap()["object"].asMap()["message"].asMap()["attachments"].asList()[0].asMap()["wall"].asMap()["id"] as Long
		if (d == 1L) // check for not to let compiler optimize code
			print("true")
		val end = System.nanoTime()
		return end - start
	}

	private inline fun Any?.asMap() = this as Map<*, *>
	private inline fun Any?.asList() = this as List<*>

	fun testIrisProxy(map: Map<String, Any?>): Long {
		val start = System.nanoTime()
		val rand = JsonProxyObject(map)
		val d = rand["object"]["message"]["attachments"][0]["wall"]["id"].asLong()
		if (d == 1L) // check for not to let compiler optimize code
			print("true")
		val end = System.nanoTime()
		return end - start
	}

	fun testIrisParserOld(test: String): Long {
		val start = System.nanoTime()
		val rand = JsonPlainParser(test).parse()
		val d = rand["object"]["message"]["attachments"][0]["wall"]["id"].asLong()
		if (d == 1L) // check for not to let compiler optimize code
			print("true")
		val end = System.nanoTime()
		return end - start
	}

	fun testIrisParser(test: String): Long {
		val start = System.nanoTime()
		val rand = JsonFlowParser.readItem(TokenerString(test))
		val d = rand["object"]["message"]["attachments"][0]["wall"]["id"].asLong()
		if (d == 1L) // check for not to let compiler optimize code
			print("true")
		val end = System.nanoTime()
		return end - start
	}

	fun testJsonParser(test: String): Long {

		val start = System.nanoTime()
		val rand = JSONObject(test)
		val d = rand.getJSONObject("object").getJSONObject("message").getJSONArray("attachments").getJSONObject(0).getJSONObject("wall").getLong("id")
		if (d == 1L) // check for not to let compiler optimize code
			print("true")
		val end = System.nanoTime()
		return end - start
	}

	fun testSimpleJsonParser(test: String): Long {
		val start = System.nanoTime()
		val rand = JSONParser().parse(test)
		val d = rand.asMap()["object"].asMap()["message"].asMap()["attachments"].asList()[0].asMap()["wall"].asMap()["id"] as Long
		if (d == 1L) // check for not to let compiler optimize code
			print("true")
		val end = System.nanoTime()
		return end - start
	}
}