package iris.json

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJson {

	enum class Type {
		Object
		, Array
		, String
		, Value
		, Null
	}

	enum class ValueType {
		Integer,
		Float,
		Constant // among them: true, false, null
	}
}

