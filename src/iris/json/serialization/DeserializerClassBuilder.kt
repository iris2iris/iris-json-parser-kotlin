package iris.json.serialization

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * @created 11.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
object DeserializerClassBuilder {
	fun build(d: KClass<*>, targetClassImpl: DeserializerClassImpl = DeserializerClassImpl()): DeserializerClassImpl {
		var hasPolymorphies = false
		val constructorInfo = getBestConstructor(d)
		val (constr, constructorFields) = constructorInfo

		val mProperties = d.memberProperties
		val fieldsList = mProperties.associateTo(HashMap(mProperties.size)) { property ->
			val objectItemName = property.name
			val jsonItemName = property.findAnnotation<JsonField>()?.name
					?.let { t -> if(t.isNotEmpty()) t else objectItemName }
					?: objectItemName
			val p = getPropertyInfo(property, constructorFields.find { it.name == objectItemName })
			if (!hasPolymorphies && p.polymorphInfo != null)
				hasPolymorphies = true
			jsonItemName to p
		}

		targetClassImpl.constructorFunction = constr
		targetClassImpl.fields = fieldsList
		targetClassImpl.hasPolymorphisms = hasPolymorphies
		return targetClassImpl
	}

	private fun getPropertyInfo(it: KProperty<*>, constructorParameter: KParameter?): DeserializerClassImpl.PropertyInfo {

		var type: DeserializerPrimitiveImpl? = null
		var inheritInfo: DeserializerClassImpl.PolymorphInfo? = null
		var innerClass: Deserializer? = null

		val data = it.findAnnotation<PolymorphData>()
		if (data != null) { // is polymorphic
			val cases = mutableMapOf<Any, Deserializer>()
			data.strings.associateTo(cases) { it.label to DeserializerFactory.getDeserializer(it.instance) }
			data.ints.associateTo(cases) { it.label to DeserializerFactory.getDeserializer(it.instance) }
			inheritInfo = DeserializerClassImpl.PolymorphInfo(data.sourceField, cases)
		} else {
			val tType = DeserializerPrimitiveImpl.convertType(it.returnType, null)
			if (tType != null) { // simple type int/string/boolean
				type = tType
			} else {
				innerClass = DeserializerFactory.getDeserializer(it.returnType)
			}
		}

		return DeserializerClassImpl.PropertyInfo(/*it.name, */it, constructorParameter, type, innerClass, inheritInfo)
	}

	private fun getBestConstructor(d: KClass<*>): Pair<KFunction<*>, List<KParameter>> {
		val constructors = d.constructors
		if (constructors.isEmpty())
			throw IllegalArgumentException("No any constructor for $d")
		var best: KFunction<*> = constructors.first()
		for (c in constructors)
			if (c.parameters.size > best.parameters.size)
				best = c
		return best to best.parameters
	}
}