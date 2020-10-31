package iris.json.serialization

import iris.json.JsonEntry
import iris.json.JsonItem
import iris.json.JsonObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.*

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class DeserializerClassImpl : DeserializerClass {

	var hasPolymorphisms = false
	lateinit var fields: Map<String, PropertyInfo>

	lateinit var constructorStrategyFactory: ConstructorStrategyFactory
	var conscructorParametersSize = 0

	class PolymorphInfo(val sourceField: String, val inheritClasses: Map<Any, Deserializer>)

	class PropertyInfo(/*val fieldName: String, */val property: KProperty<*>, val constructorParameter: KParameter? = null
					   , val type: DeserializerPrimitiveImpl?
					   , val customClass: Deserializer? = null
					   , val polymorphInfo: PolymorphInfo? = null
	)

	interface ConstructorStrategyFactory {
		fun getInstance(): ConstructorStrategy
	}

	interface ConstructorStrategy {
		fun setParameter(constructorParameter: KParameter, value: Any?)
		fun execute(): Any?
	}

	class EmptyConstructorStrategyFactory(private val constructorFunction: KFunction<*>) : ConstructorStrategyFactory, ConstructorStrategy {
		override fun getInstance() = this

		override fun setParameter(constructorParameter: KParameter, value: Any?) {}

		override fun execute(): Any? {
			return constructorFunction.call()
		}
	}

	class RawConstructorStrategyFactory(private val constructorFunction: KFunction<*>, private val params: Array<Any?>) : ConstructorStrategyFactory {
		override fun getInstance(): ConstructorStrategy {
			return RawConstructorStrategy()
		}

		inner class RawConstructorStrategy : ConstructorStrategy {

			private val cParams = params.copyOf()

			override fun setParameter(constructorParameter: KParameter, value: Any?) {
				cParams[constructorParameter.index] = value
			}

			override fun execute(): Any? {
				return constructorFunction.call(*cParams)
			}
		}
	}

	class MapConstructorStrategyFactory(private val constructorFunction: KFunction<*>, private val constructorParametersSize: Int) : ConstructorStrategyFactory {
		override fun getInstance(): ConstructorStrategy {
			return MapConstructorStrategy()
		}

		inner class MapConstructorStrategy : ConstructorStrategy {

			private val map = HashMap<KParameter, Any?>(constructorParametersSize)

			override fun setParameter(constructorParameter: KParameter, value: Any?) {
				map[constructorParameter] = value
			}

			override fun execute(): Any? {
				return constructorFunction.callBy(map)
			}
		}
	}




	class SetterPropertyException(message: String, cause: Throwable) : Throwable(message, cause)

	override fun <T> deserialize(item: JsonItem): T {
		return getObject((item as JsonObject).getEntries())
	}

	override fun forSubclass(d: KClass<*>): Deserializer {
		return this
	}

	override fun <T>getObject(entries: Collection<JsonEntry>): T {
		val info = this
		val fields = info.fields
		val hasPolymorphisms = info.hasPolymorphisms
		val delayedInit: MutableMap<String, Delayed>?
		val result: MutableMap<String, Any?>?
		if (hasPolymorphisms) {
			delayedInit = mutableMapOf()
			result = mutableMapOf()
		} else {
			delayedInit = null
			result = null
		}

		//val conscructorParametersSize = constructorParametersList.size
		val otherSize = fields.size - conscructorParametersSize
		//val constructorMap = HashMap<KParameter, Any?>(conscructorParametersSize)
		//val constructorList = constructorParametersList.copyOf()

		val constructorStrategy = constructorStrategyFactory.getInstance()

		val otherFields = (if (otherSize == 0)
			null
		else
			ArrayList<Pair<KProperty<*>, Any?>>(fields.size - conscructorParametersSize))

		for ((key, jsonItem) in entries) {
			val field = key.toString()
			val param = fields[field]?: continue
			val polymorphInfo = param.polymorphInfo
			if (polymorphInfo != null) {
				val sourceValue = result!![polymorphInfo.sourceField]
				if (sourceValue != null) { // already know what type is it
					val inherit = polymorphInfo.inheritClasses[sourceValue]!!
					val newValue: Any? = inherit.deserialize(jsonItem)
					result[field] = newValue
					param.constructorParameter?.let { constructorStrategy.setParameter(it, newValue)/*constructorList[it.order] = newValue*/ }
							?: run { otherFields!! += param.property to newValue }

				} else { // need delay initialization until we know source info
					val item = delayedInit!![polymorphInfo.sourceField]
					if (item == null)
						delayedInit[polymorphInfo.sourceField] = Delayed(Delayed.Data(param, jsonItem))
					else
						item.add(Delayed.Data(param, jsonItem))
				}
			} else {
				val value = getValue(jsonItem, param)
				if (delayedInit != null) {
					val delayed = delayedInit[field]
					if (delayed != null) { // yes! at last we have delayed information!
						val item = delayed.firstItem
						val property = item.propertyInfo
						val inherit = property.polymorphInfo!!.inheritClasses[value]!!
						val newValue: Any? = inherit.deserialize(item.json)
						item.propertyInfo.constructorParameter?.let { constructorStrategy.setParameter(it, newValue)/* constructorList[it.order] = newValue*/ }
								?: run { otherFields!! += property.property to newValue }

						if (delayed.items != null) {
							for (item in delayed.items!!) {
								val property = item.propertyInfo
								val inherit = property.polymorphInfo!!.inheritClasses[value]!!
								val newValue: Any = inherit.deserialize(item.json)
								item.propertyInfo.constructorParameter?.let { constructorStrategy.setParameter(it, newValue) /*constructorList[it.order] = newValue*/ }
										?: run { otherFields!! += property.property to newValue }
							}
						}
					}
					result!![field] = value
				}

				param.constructorParameter?.let { constructorStrategy.setParameter(it, value)/* constructorList[it.order] = value*/ }
						?: run{ otherFields!! += param.property to value }
			}
		}
		//val item = info.constructorFunction.call(constructorList) as T
		val item = constructorStrategy.execute() as T
		if (otherFields != null) {
			for (field in otherFields) {
				try {
					(field.first as KMutableProperty<*>).setter.call(item, field.second)
				} catch (e: ClassCastException) {
					throw SetterPropertyException("Property $field does not have available setter", e)
				}
			}
		}
		return item
	}

	private class Delayed(val firstItem: Data) {
		class Data(val propertyInfo: PropertyInfo, val json: JsonItem)

		var items: MutableList<Data>? = null
		fun add(item: Data) {
			if (items == null)
				items = LinkedList()
			items!! += item
		}
	}

	private fun getValue(value: JsonItem, property: PropertyInfo): Any? {
		property.type?.let {
			return it.getValue(value)
		}

		property.customClass?.let {
			return it.deserialize(value)
		}
		throw IllegalStateException("How we got here?")
	}
}