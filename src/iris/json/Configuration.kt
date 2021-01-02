package iris.json

/**
 * @created 30.12.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class Configuration {

	companion object {
		val globalConfiguration = Configuration()
	}

	interface MapObjectFactory {
		fun getMap(elementsAmount: Int): MutableMap<String, Any?>
	}

	var trailingCommaAllowed: Boolean = true

	var mapObjectFactory: MapObjectFactory = object : MapObjectFactory {
		override fun getMap(elementsAmount: Int): MutableMap<String, Any?> {
			return HashMap(elementsAmount)
		}
	}
}