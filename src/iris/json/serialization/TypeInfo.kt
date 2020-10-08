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

class TypeInfo(val type: Type) : NodeInfo {

	enum class Type {
		INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, STRING, DATE
	}
	
	companion object {
		val DATE = TypeInfo(Type.DATE)
		val INTEGER = TypeInfo(Type.INTEGER)
		val LONG = TypeInfo(Type.LONG)
		val DOUBLE = TypeInfo(Type.DOUBLE)
		val FLOAT = TypeInfo(Type.FLOAT)
		val BOOLEAN = TypeInfo(Type.BOOLEAN)
		val STRING = TypeInfo(Type.STRING)

		private val format = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")

		fun convertType(s: KType, fieldData: Field?): TypeInfo? {
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
					"datetime" -> DATE
					"date" -> DATE
					"integer", "int" -> INTEGER
					"long" -> LONG
					"double" -> DOUBLE
					"float" -> FLOAT
					"bool", "boolean" -> BOOLEAN
					"string", "text" -> STRING
					else -> null
				}
			}
		}
	}

	fun getValue(value: JsonItem): Any? {
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