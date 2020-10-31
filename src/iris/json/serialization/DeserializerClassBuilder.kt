package iris.json.serialization

import iris.json.serialization.DeserializerClassImpl.*
import kotlin.reflect.*
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
		val (strategyFactory, constructorFields) = constructorInfo
		val mProperties = d.memberProperties
		val fieldsList = mProperties.associateTo(HashMap(mProperties.size)) { property ->
			val objectItemName = property.name
			val jsonItemName = property.findAnnotation<JsonField>()?.name
					?.let { t -> if(t.isNotEmpty()) t else objectItemName }
					?: objectItemName
			val p = getPropertyInfo(property, constructorFields[objectItemName])
			if (!hasPolymorphies && p.polymorphInfo != null)
				hasPolymorphies = true
			jsonItemName to p
		}

		targetClassImpl.constructorStrategyFactory = strategyFactory
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

		return PropertyInfo(/*it.name, */it, constructorParameter, type, innerClass, inheritInfo)
	}

	private fun getBestConstructor(d: KClass<*>): Pair<ConstructorStrategyFactory, Map<String, KParameter>> {
		return if (d.java.isAnnotationPresent(Metadata::class.java)) buildParametersByMetadata(d) else buildParametersInClassOrder(d)
	}

	private fun buildParametersByMetadata(d: KClass<*>): Pair<ConstructorStrategyFactory, Map<String, KParameter>> {
		val constructors = d.constructors
		if (constructors.isEmpty())
			throw IllegalArgumentException("No any constructor for $d")
		var best: KFunction<*> = constructors.first()
		for (c in constructors)
			if (c.parameters.size > best.parameters.size)
				best = c
		val parameters = best.parameters
		return (if (parameters.isEmpty()) EmptyConstructorStrategyFactory(best) else MapConstructorStrategyFactory(best, parameters.size)) to parameters.associateBy { it.name!! }
	}

	private fun buildParametersInClassOrder(d: KClass<*>): Pair<ConstructorStrategyFactory, Map<String, KParameter>> {
		val targetConstructorSize = d.memberProperties.size
		val constructor = findZeroOrTargetConstructor(d, targetConstructorSize)
		val strategyFactory = if (constructor.parameters.isEmpty())EmptyConstructorStrategyFactory(constructor) else RawConstructorStrategyFactory(constructor, buildParams(constructor.parameters))
		val map = constructor.parameters.associateBy { it.name!! }
		return strategyFactory to map
	}

	private fun buildParams(parameters: List<KParameter>): Array<Any?> {
		return Array(parameters.size) {
			buildDefault(parameters[it])
		}
	}

	private fun buildDefault(kParameter: KParameter): Any? {
		return when (val type = kParameter.type) {
			typeOf<Int>() -> if (type.isMarkedNullable) null else 0
			typeOf<Long>() -> if (type.isMarkedNullable) null else 0L
			typeOf<Double>() -> if (type.isMarkedNullable) null else 0.0
			typeOf<Float>() -> if (type.isMarkedNullable) null else 0f
			typeOf<Boolean>() -> if (type.isMarkedNullable) null else false
			else -> null
		}
	}

	private fun findZeroOrTargetConstructor(d: KClass<*>, targetConstructorSize: Int): KFunction<*> {
		val constructors = d.constructors
		if (constructors.isEmpty())
			throw IllegalArgumentException("No any constructor for $d")
		for (c in constructors) {
			if (c.parameters.isEmpty())
				return c
			if (c.parameters.size == targetConstructorSize)
				return c
		}
		throw IllegalArgumentException("No constructor for $d with $targetConstructorSize or no arguments size")
	}
}