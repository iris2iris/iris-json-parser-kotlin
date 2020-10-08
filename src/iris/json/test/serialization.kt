package iris.json.test

import iris.json.plain.IrisJsonParser
import iris.json.serialization.PolymorphCaseString
import iris.json.serialization.PolymorphData

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

fun main() {
	testUser(); println()
	testListInt(); println()
	testListListInt(); println()
	testListedData(); println()
}

fun testListListInt() {
	println("testListListInt:")
	val parser = IrisJsonParser("""{
		|"listed":[
		|[1,2,3,4,5]
		|,[6,7,8,9,10]
		|]}""".trimMargin())
	val item = parser.parse()
	val list = item.asObject(ListedListInt::class)
	println(list)
}

fun testListInt() {
	println("testListInt:")
	val parser = IrisJsonParser("""{
		|"listed":[1,2,3,4,5]}""".trimMargin())
	val item = parser.parse()
	val list = item.asObject(ListedInt::class)
	println(list)
}

fun testListedData() {
	println("testListedData:")
	val parser = IrisJsonParser("""{
		|"listed":[
		|{"name": "Alla Who", "age": 35, "cashAmount": 122.12121, "property": {"name": "Домик в деревне"}}
		|, {"name": "Akbar", "age": 46, "cashAmount": 44.3, "property": {"name": "Деревня"}}
		|]}""".trimMargin())
	val item = parser.parse()
	val list = item.asObject(ListedClass::class)
	println(list)
}

fun testUser() {
	println("testUser:")
	val parser = IrisJsonParser("""{"id": 3, 
		|"person1": {"name": "Akbar", "age": 35, "cashAmount": 12200.12, "property": {"name": "Домик в деревне"}}, 
		|"type": "MaleFirst", 
		|"person2": {"name": "Alla Who", "height": 170, "income": 1214.81}
		|}""".trimMargin())

	val item = parser.parse()
	val user = item.asObject(User::class)
	println(user.person1)
	println(user.person2)

}