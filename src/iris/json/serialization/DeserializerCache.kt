package iris.json.serialization

import iris.json.JsonItem
import iris.json.serialization.DeserializerClassImpl.PolymorphInfo
import iris.json.serialization.DeserializerClassImpl.PropertyInfo
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

/**
 * @created 05.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

object DeserializerCache {
	private val cache = mutableMapOf<KClass<*>, Deserializer>()
	private val typeCache = mutableMapOf<KType, Deserializer>()

	fun getDeserializer(d: KClass<*>): Deserializer {
		return cache.getOrPut(d) { buildInstance(d) }
	}

	fun getDeserializer(type: KType): Deserializer {
		return typeCache.getOrPut(type) {
			DeserializerPrimitiveImpl.convertType(type, null)
				?.let {
					return@getOrPut it
				}

			with(type.jvmErasure) { when {
					isSubclassOf(Collection::class) ->
						DeserializerCollectionImpl(getDeserializer(type.arguments.firstOrNull()?.type?: throw IllegalStateException("Don't know how I got here")))
					isSubclassOf(Map::class) ->
						DeserializerMapImpl(getDeserializer(getMapType(type)))
					isSubclassOf(JsonItem::class) ->
						DeserializerJsonItem()
					else ->
						getDeserializer(this)
			}}
		}
	}

	private fun getMapType(type: KType): KType {
		val (key, value) = type.arguments
		if (!key.type!!.isSubtypeOf(CharSequence::class.starProjectedType))
			throw IllegalStateException("Map key cannot be not CharSequence inherited")
		return value.type!!
	}

	private fun buildInstance(d: KClass<*>): DeserializerClassImpl {
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

	private fun getPropertyInfo(it: KProperty<*>, constructorParameter: KParameter?): PropertyInfo {
		val tType = DeserializerPrimitiveImpl.convertType(it.returnType, null)
		var type: DeserializerPrimitiveImpl? = null
		var inheritInfo: PolymorphInfo? = null
		var innerClass: Deserializer? = null
		if (tType != null) { // simple type int/string/boolean
			type = tType
		} else { // there some complex class
			val data = it.findAnnotation<PolymorphData>()
			if (data != null) { // is polymorphic
				val cases = mutableMapOf<Any, Deserializer>()
				data.strings.associateTo(cases) { it.label to getDeserializer(it.instance) }
				data.ints.associateTo(cases) { it.label to getDeserializer(it.instance) }
				inheritInfo = PolymorphInfo(data.sourceField, cases)
			} else {
				innerClass = getDeserializer(it.returnType)
			}
		}

		return PropertyInfo(/*it.name, */it, constructorParameter, type, innerClass, inheritInfo)
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
