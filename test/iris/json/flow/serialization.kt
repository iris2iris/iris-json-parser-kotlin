package iris.json.flow

import iris.json.JsonItem
import iris.json.asObject
import iris.json.serialization.*
import kotlin.reflect.KClass

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class User(val id: Int, type: String) {

	val type = type

	@PolymorphData(
			sourceField = "type"
			, strings = [PolymorphCaseString(label = "MaleFirst", instance = Male::class), PolymorphCaseString(label = "FemaleFirst", instance = Female::class)]
	)
	var person1: Human? = null

	@PolymorphData(
			sourceField = "type"
			, strings = [PolymorphCaseString(label = "FemaleFirst", instance = Male::class), PolymorphCaseString(label = "MaleFirst", instance = Female::class)]
	)
	var person2: Human? = null
}

open class Human
data class Male(val name: String, val age: Int = 1000, val cashAmount: Double = 1333.0, val property: Property? = null): Human()
data class Female(val name: String, val height: Int, val income: Double, val property: Property? = null): Human()

data class Property(val name: String)

data class ListedClass(val listed: List<Male>)
data class ListedInt(val listed: List<Int>)
data class ListedListInt(val listed: List<List<Int>>)

data class DefinedJsonField(
	@JsonField(name = "object")
	val obj: String
)

fun main() {
	println("*** Iris Json Flow test ***\n")

	testUser(); println()
	testListInt(); println()
	testListListInt(); println()
	testListedData(); println()
	testPureList(); println()
	testPrimitive(); println()
	testMap(); println()
	testJsonItem(); println()
	testDefinedJsonField(); println()
	testRegisteredDeserializer(); println()
	testQuotelessFieldNames(); println()
	testSubclassRegister(); println()
	testRecursiveClassFields(); println()
	testRecursiveGenericClassFields(); println()
}

fun createJsonItem(text: String): JsonItem {
	return JsonFlowParser.start(text)
}

data class RecursiveGenericClass (
		var inner: List<RecursiveGenericClass>? = null,
		var someData: String? = null
)

fun testRecursiveGenericClassFields() {
	println("testRecursiveGenericClassFields:")
	val item = createJsonItem("""{ 
		inner: [{inner: [{someData: "3 level"}], someData: "2 level"}], someData: "1 level"		
		}""".trimMargin())
	val list = item.asObject<RecursiveGenericClass>()
	println(list)
}

data class RecursiveClass (
		var inner: RecursiveClass? = null,
		var someData: String? = null
)

fun testRecursiveClassFields() {
	println("testRecursiveClassFields:")
	val item = createJsonItem("""{ 
		inner: {inner: {someData: "3 level"}, someData: "2 level"}, someData: "1 level"		
		}""".trimMargin())
	val list = item.asObject<RecursiveClass>()
	println(list)
}

fun testSubclassRegister() {
	println("testSubclassRegister:")

	DeserializerFactory.registerDeserializer(iris.json.plain.Human::class, object : Deserializer {

		private val maleDeserializer = DeserializerFactory.getDeserializer(iris.json.plain.Male::class, false)
		private val femaleDeserializer = DeserializerFactory.getDeserializer(iris.json.plain.Female::class, false)

		override fun <T> deserialize(item: JsonItem): T {
			val deserializer = when (item["type"].asString()) {
				"male" -> maleDeserializer
				"female" -> femaleDeserializer
				else -> throw IllegalArgumentException("There are only 2 genders")
			}
			return deserializer.deserialize(item)
		}

		override fun forSubclass(d: KClass<*>): Deserializer {
			return this
		}
	})

	val item = JsonFlowParser.start("""{ 
		|person1: {"type": "male", name: "Akbar", age: 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|"person2": {"type": "female","name": "Alla Who", "height": 170, "income": 1214.81}
		|}""".trimMargin())
	val list = item.asObject<Map<String, iris.json.plain.Human>>()
	println(list)
}

fun testQuotelessFieldNames() {
	println("testQuotelessFieldNames:")
	val item = JsonFlowParser.start("""{ 
		|person1: {name: "Akbar", age: 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|person2: {name: "Alla Who", age: 15, "cashAmount": 23232.12, "property": {"name": "В центре высотка"}} 
		|}""".trimMargin())
	val list = item.asObject<Map<String, Male>>()
	println(list)
}

class EnumDeserializer : DeserializerPrimitive {

	enum class YesNoEnum(val value: String) {
		Yes("yes"), No("no")
	}

	override fun getValue(item: JsonItem): Any? {
		return deserialize(item)
	}

	override fun <T> deserialize(item: JsonItem): T {
		return when(item.asString()) {
			"yes" -> YesNoEnum.Yes
			"no" -> YesNoEnum.No
			else -> null
		} as T
	}

	override fun forSubclass(d: KClass<*>): Deserializer {
		return this
	}
}

fun testRegisteredDeserializer() {
	println("testRegisteredDeserializer:")
	DeserializerFactory.registerDeserializer(EnumDeserializer.YesNoEnum::class, EnumDeserializer())
	val item = JsonFlowParser.start("""
		{ 
			"drunk": "yes",
			"enough": "no" 
		}""".trimMargin())
	val list = item.asObject<Map<String, EnumDeserializer.YesNoEnum>>()
	println(list)
}

fun testDefinedJsonField() {
	println("testDefinedJsonField:")
	val item = JsonFlowParser.start("""{ 
		"object": "Other field name" 
		}""".trimMargin())

	val list = item.asObject<DefinedJsonField>()
	println(list)
}

fun testJsonItem() {
	println("testJsonItem:")
	val item = JsonFlowParser.start("""{ 
		|"person1": {"name": "Akbar", "age": 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|"person2": {"name": "Alla Who", "age": 15, "cashAmount": 23232.12, "property": {"name": "В центре высотка"}} 
		|}""".trimMargin())
	val list = item.asObject<Map<String, JsonItem>>()
	println(list)
}

fun testMap() {
	println("testMap:")
	val item = JsonFlowParser.start("""{ 
		|"person1": {"name": "Akbar", "age": 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|"person2": {"name": "Alla Who", "age": 15, "cashAmount": 23232.12, "property": {"name": "В центре высотка"}} 
		|}""".trimMargin())
	val list = item.asObject<Map<String, Male>>()
	println(list)
}

fun testPrimitive() {
	println("testPrimitive:")
	val item = JsonFlowParser.start("""1""".trimMargin())
	val list = item.asObject<Int>()
	println(list)
}

fun testPureList() {
	println("testPureList:")
	val item = JsonFlowParser.start("""[
		|1,2,3,4,5
		|,6,7,8,9,10
		|]""".trimMargin())
	val list = item.asObject<List<Int>>()
	println(list)
}

fun testListListInt() {
	println("testListListInt:")
	val item = JsonFlowParser.start("""{
		|"listed":[
		|[1,2,3,4,5]
		|,[6,7,8,9,10]
		|]}""".trimMargin())
	val list = item.asObject<ListedListInt>()
	println(list)
}

fun testListInt() {
	println("testListInt:")
	val item = JsonFlowParser.start("""{
		|"listed":[1,2,3,4,5]}""".trimMargin())
	val list = item.asObject<ListedInt>()
	println(list)
}

fun testListedData() {
	println("testListedData:")
	val item = JsonFlowParser.start("""{
		|"listed":[
		|{"name": "Alla Who", "age": 35, "cashAmount": 122.12121, "property": {"name": "Домик в деревне"}}
		|, {"name": "Akbar", "age": 46, "cashAmount": 44.3, "property": {"name": "Деревня"}}
		|]}""".trimMargin())
	val list = item.asObject<ListedClass>()
	println(list)
}

fun testUser() {
	println("testUser:")
	val item = JsonFlowParser.start("""{"id": 3, 
		|"person1": {"name": "Akbar", "age": 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|"type": "MaleFirst", 
		|"person2": {"name": "Alla Who", "height": 170, "income": 1214.81}
		|}""".trimMargin())

	val user = item.asObject<User>()
	println(user.person1)
	println(user.person2)

}