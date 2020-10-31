package iris.json.serialization

import iris.json.JsonItem
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmErasure

/**
 * @created 05.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

object DeserializerFactory {
	private val cache = mutableMapOf<KClass<*>, Deserializer>()
	private val typeCache = mutableMapOf<KType, Deserializer>()
	private val anyClass = Any::class

	fun getDeserializer(d: KClass<*>, allowSuperclasses: Boolean = true): Deserializer {
		cache[d]?.let { return it }
		if (allowSuperclasses) {
			for (supers in d.superclasses) {
				if (supers == anyClass) continue
				cache[supers]?.let {
					return it.forSubclass(d).also { cache[d] = it }
				}
			}
		}
		val deser = DeserializerClassImpl()
		cache[d] = deser
		return DeserializerClassBuilder.build(d, deser)
	}

	fun registerDeserializer(d: KClass<*>, deserializer: Deserializer) {
		cache[d] = deserializer
	}

	fun registerDeserializer(type: KType, deserializer: Deserializer) {
		typeCache[type] = deserializer
	}

	fun getDeserializer(type: KType): Deserializer {
		typeCache[type]
			?.let { return it }

		DeserializerPrimitiveImpl.convertType(type, null)
			?.let {
				return it.also { typeCache[type] = it }
			}

		return with(type.jvmErasure) { when {
			isSubclassOf(Collection::class) -> {
				val deser = DeserializerCollectionImpl()
				typeCache[type] = deser
				deser.typeDeserializer = getDeserializer(type.arguments.firstOrNull()?.type?: throw IllegalStateException("Don't know how I got here"))
				deser
			}
			isSubclassOf(Map::class) -> {
				val deser = DeserializerMapImpl()
				typeCache[type] = deser
				deser.valueDeserializer = getDeserializer(getMapType(type))
				deser
			}
			isSubclassOf(JsonItem::class) ->
				DeserializerJsonItem()
			else ->
				getDeserializer(this)
		}}
	}

	private fun getMapType(type: KType): KType {
		val (key, value) = type.arguments
		if (!key.type!!.isSubtypeOf(CharSequence::class.starProjectedType))
			throw IllegalStateException("Map key cannot be non CharSequence inherited")
		return value.type!!
	}
}
