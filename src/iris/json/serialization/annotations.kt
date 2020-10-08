package iris.json.serialization

import kotlin.reflect.KClass

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Field(val name: String = "", val type: String = "")

@Target(AnnotationTarget.PROPERTY)
annotation class PolymorphCaseString(val label: String, val instance: KClass<*>)

@Target(AnnotationTarget.PROPERTY)
annotation class PolymorphCaseInt(val label: Int, val instance: KClass<*>)

annotation class PolymorphData(val sourceField: String, val strings: Array<PolymorphCaseString> = [], val ints: Array<PolymorphCaseInt> = [])