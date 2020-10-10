package iris.json.serialization

import iris.json.JsonItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class DeserializerPrimitiveImpl(val type: Type) : DeserializerPrimitive {

	enum class Type {
		INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, STRING, DATE
	}
	
	companion object {
		val DATE = DeserializerPrimitiveImpl(Type.DATE)
		val INTEGER = DeserializerPrimitiveImpl(Type.INTEGER)
		val LONG = DeserializerPrimitiveImpl(Type.LONG)
		val DOUBLE = DeserializerPrimitiveImpl(Type.DOUBLE)
		val FLOAT = DeserializerPrimitiveImpl(Type.FLOAT)
		val BOOLEAN = DeserializerPrimitiveImpl(Type.BOOLEAN)
		val STRING = DeserializerPrimitiveImpl(Type.STRING)

		private val format = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")

		fun convertType(s: KType, fieldData: JsonField?): DeserializerPrimitiveImpl? {
			return if (fieldData?.type.isNullOrBlank()) {
				when (s.javaType.typeName) {
					"java.util.Date" -> DATE
					"int", "java.lang.Integer" -> INTEGER
					"long", "java.lang.Long" -> LONG
					"double", "java.lang.Double" -> DOUBLE
					"float", "java.lang.Float" -> FLOAT
					"boolean", "java.lang.Boolean" -> BOOLEAN
					"java.lang.String" -> STRING
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
					else -> null
				}
			}
		}
	}

	override fun <T : Any> deserialize(item: JsonItem): T {
		return getValue(item) as T
	}

	override fun getValue(value: JsonItem): Any? {
		return when (type) {
			Type.INTEGER -> value.asIntOrNull()
			Type.LONG -> value.asLongOrNull()
			Type.DOUBLE -> value.asDoubleOrNull()
			Type.FLOAT ->  value.asFloatOrNull()
			Type.BOOLEAN ->  value.asBooleanOrNull()
			Type.STRING ->  value.asStringOrNull()
			Type.DATE ->  when (val obj = value.obj()) {
				is Number -> Date(obj.toLong()*1000L)
				is String -> format.parse(obj)
				else -> obj
			}
			//else -> value.obj()
		}
	}
}