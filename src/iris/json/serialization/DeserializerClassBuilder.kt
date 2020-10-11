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
	fun build(d: KClass<*>): DeserializerClassImpl {
		var hasPolymorphies = false
		val constructorInfo = getFieldsOrder(d.constructors)
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

		return DeserializerClassImpl(constr, fieldsList, hasPolymorphies)
	}

	private fun getPropertyInfo(it: KProperty<*>, constructorParameter: KParameter?): DeserializerClassImpl.PropertyInfo {
		val tType = DeserializerPrimitiveImpl.convertType(it.returnType, null)
		var type: DeserializerPrimitiveImpl? = null
		var inheritInfo: DeserializerClassImpl.PolymorphInfo? = null
		var innerClass: Deserializer? = null
		if (tType != null) { // simple type int/string/boolean
			type = tType
		} else { // there some complex class
			val data = it.findAnnotation<PolymorphData>()
			if (data != null) { // is polymorphic
				val cases = mutableMapOf<Any, Deserializer>()
				data.strings.associateTo(cases) { it.label to DeserializerFactory.getDeserializer(it.instance) }
				data.ints.associateTo(cases) { it.label to DeserializerFactory.getDeserializer(it.instance) }
				inheritInfo = DeserializerClassImpl.PolymorphInfo(data.sourceField, cases)
			} else {
				innerClass = DeserializerFactory.getDeserializer(it.returnType)
			}
		}

		return DeserializerClassImpl.PropertyInfo(/*it.name, */it, constructorParameter, type, innerClass, inheritInfo)
	}

	private fun getFieldsOrder(constructors: Collection<KFunction<*>>): Pair<KFunction<*>, List<KParameter>> {
		var best: KFunction<*>? = null
		for (c in constructors)
			if (best == null || c.parameters.size > best.parameters.size)
				best = c
		if (best == null)
			throw IllegalArgumentException("No any constructor")
		return best to best.parameters
	}
}