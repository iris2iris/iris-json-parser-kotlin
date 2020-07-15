package iris.json

/** Создано 14.04.2020 */


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
		Constant // в том числе: true, false, null
	}
}

