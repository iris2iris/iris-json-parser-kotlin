package iris.json.serialization

import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class PropertyInfo(val fieldName: String, val property: KProperty<*>, val constructorParameter: KParameter? = null
		   , val type: TypeInfo?
		   , val customClass: NodeInfo? = null
		   , val polymorphInfo: PolymorphInfo? = null
)