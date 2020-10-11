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

	fun getDeserializer(d: KClass<*>, allowSuperclasses: Boolean = true): Deserializer {
		return cache.getOrPut(d) {
			if (allowSuperclasses) {
				for (supers in d.superclasses)
					cache[supers]?.let { return@getOrPut it.forSubclass(d) }
			}
			DeserializerClassBuilder.build(d)
		}
	}

	fun registerDeserializer(d: KClass<*>, deserializer: Deserializer) {
		cache[d] = deserializer
	}

	fun getDeserializer(type: KType): Deserializer {
		return typeCache.getOrPut(type) {
			DeserializerPrimitiveImpl.convertType(type, null)
				?.let { return@getOrPut it }

			with(type.jvmErasure) { when {
					isSubclassOf(Collection::class) ->
						DeserializerCollectionImpl(getDeserializer(type.arguments.firstOrNull()?.type?: throw IllegalStateException("Don't know how I got here")))
					isSubclassOf(Map::class) ->
						DeserializerMapImpl(getDeserializer(getMapType(type)))
					isSubclassOf(JsonItem::class) ->
						DeserializerJsonItem()
					else ->
						getDeserializer(this)
			}}
		}
	}

	private fun getMapType(type: KType): KType {
		val (key, value) = type.arguments
		if (!key.type!!.isSubtypeOf(CharSequence::class.starProjectedType))
			throw IllegalStateException("Map key cannot be non CharSequence inherited")
		return value.type!!
	}
}
