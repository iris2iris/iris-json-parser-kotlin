package iris.json.serialization

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class PolymorphInfo(val sourceField: String, val inheritClasses: Map<Any, NodeInfo>)