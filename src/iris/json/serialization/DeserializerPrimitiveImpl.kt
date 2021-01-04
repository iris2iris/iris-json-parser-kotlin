package iris.json.serialization

import iris.json.JsonItem
import iris.json.serialization.DeserializerPrimitive.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class DeserializerPrimitiveImpl(private val type: Type) : DeserializerPrimitive {

	companion object {
		private val DATE = DeserializerPrimitiveImpl(Type.DATE)
		private val INTEGER = DeserializerPrimitiveImpl(Type.INTEGER)
		private val LONG = DeserializerPrimitiveImpl(Type.LONG)
		private val DOUBLE = DeserializerPrimitiveImpl(Type.DOUBLE)
		private val FLOAT = DeserializerPrimitiveImpl(Type.FLOAT)
		private val BOOLEAN = DeserializerPrimitiveImpl(Type.BOOLEAN)
		private val STRING = DeserializerPrimitiveImpl(Type.STRING)
		private val ANY = DeserializerPrimitiveImpl(Type.ANY)

		private val dateClass = Date::class
		private val intClass = Int::class
		private val longClass = Long::class
		private val doubleClass = Double::class
		private val floatClass = Float::class
		private val booleanClass = Boolean::class
		private val stringClass = String::class
		private val anyClass = Any::class

		private val format = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")

		fun convertType(s: KType, fieldData: JsonField?): DeserializerPrimitiveImpl? {
			return if (fieldData?.type.isNullOrBlank()) {
				when (s.jvmErasure) {
					dateClass -> DATE
					intClass -> INTEGER
					longClass -> LONG
					doubleClass -> DOUBLE
					floatClass -> FLOAT
					booleanClass -> BOOLEAN
					stringClass -> STRING
					anyClass -> ANY
					else -> null
				}
			} else {
				when (fieldData!!.type) {
					"Datetime" -> DATE
					"Date" -> DATE
					"Integer", "Int" -> INTEGER
					"Long" -> LONG
					"Double" -> DOUBLE
					"Float" -> FLOAT
					"bool", "Boolean" -> BOOLEAN
					"String", "Text" -> STRING
					"Any" -> ANY
					else -> null
				}
			}
		}
	}

	override fun <T> deserialize(item: JsonItem): T {
		return getValue(item) as T
	}

	override fun forSubclass(d: KClass<*>): Deserializer {
		return this
	}

	override fun getValue(item: JsonItem): Any? {
		return when (type) {
			Type.INTEGER -> item.asIntOrNull()
			Type.LONG -> item.asLongOrNull()
			Type.DOUBLE -> item.asDoubleOrNull()
			Type.FLOAT ->  item.asFloatOrNull()
			Type.BOOLEAN ->  item.asBooleanOrNull()
			Type.STRING ->  item.asStringOrNull()
			Type.DATE ->  when (val obj = item.obj()) {
				is Number -> Date(obj.toLong()*1000L)
				is String -> format.parse(obj)
				else -> obj
			}
			Type.ANY -> item.obj()
			//else -> item.obj()
		}
	}
}