package iris.json.serialization

import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

/**
 * @created 05.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

object SerializationCache {
	private val cache = mutableMapOf<KClass<*>, ClassInfo>()

	fun getInstance(d: KClass<*>): NodeInfo {
		return cache.getOrPut(d) { buildInstance(d) }
	}

	private fun buildInstance(d: KClass<*>): ClassInfo {
		var hasPolymorphies = false
		val constructorInfo = getFieldsOrder(d.constructors)
		val (constr, constructorFields) = constructorInfo

		val fieldsList = d.memberProperties.associateTo(mutableMapOf()) {
			val name = it.name
			val p = getProperty(it, constructorFields.find { it.name == name })
			if (!hasPolymorphies && p.polymorphInfo != null)
				hasPolymorphies = true
			name to p
		}

		return ClassInfo(constr, fieldsList, hasPolymorphies)
	}

	private fun getProperty(it: KProperty<*>, constructorParameter: KParameter?): PropertyInfo {
		val tType = TypeInfo.convertType(it.returnType, null)
		var type: TypeInfo? = null
		var inheritInfo: PolymorphInfo? = null
		var innerClass: NodeInfo? = null
		if (tType != null) { // simple type int/string/boolean
			type = tType
		} else { // there some complex class
			val data = it.findAnnotation<PolymorphData>()
			if (data != null) { // is polymorphic
				val cases = mutableMapOf<Any, NodeInfo>()
				data.strings.associateTo(cases) { it.label to getInstance(it.instance) }
				data.ints.associateTo(cases) { it.label to getInstance(it.instance) }
				inheritInfo = PolymorphInfo(data.sourceField, cases)
			} else {
				val kClass =  it.returnType
				innerClass = if (kClass.jvmErasure.isSubclassOf(Collection::class)) {
					ListInfo(getListInfo(kClass))
				} else
					getInstance(kClass.jvmErasure)
			}
		}

		return PropertyInfo(it.name, it, constructorParameter, type, innerClass, inheritInfo)
	}

	private fun getListInfo(type: KType): NodeInfo {
		val retType = type.arguments.firstOrNull()?.type ?: throw IllegalStateException("Don't know what")
		TypeInfo.convertType(retType, null)
			?.let {
				return it
			}

		val kClass = retType.jvmErasure
		if (kClass.isSubclassOf(Collection::class))
			return ListInfo(getListInfo(retType))

		return getInstance(kClass)
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
