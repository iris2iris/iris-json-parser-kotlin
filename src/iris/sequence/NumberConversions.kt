@file:Suppress("NOTHING_TO_INLINE")

package iris.sequence

public inline fun CharSequence?.toBoolean(): Boolean = java.lang.Boolean.parseBoolean(this.toString())

public inline fun CharSequence.toByte(): Byte = java.lang.Byte.parseByte(this.toString())

public inline fun CharSequence.toByte(radix: Int): Byte = java.lang.Byte.parseByte(this.toString(), checkRadix(radix))

public inline fun CharSequence.toShort(): Short = java.lang.Short.parseShort(this.toString())

public inline fun CharSequence.toShort(radix: Int): Short = java.lang.Short.parseShort(this.toString(), checkRadix(radix))

public inline fun CharSequence.toInt(): Int = this.toInt(10)

public inline fun CharSequence.toInt(radix: Int): Int = java.lang.Integer.parseInt(this, 0, this.length, checkRadix(radix))

public inline fun CharSequence.toLong(): Long = this.toLong(10)

public inline fun CharSequence.toLong(radix: Int): Long = java.lang.Long.parseLong(this, 0, this.length, checkRadix(radix))

public inline fun CharSequence.toFloat(): Float = java.lang.Float.parseFloat(this.toString())

public inline fun CharSequence.toDouble(): Double = java.lang.Double.parseDouble(this.toString())

public fun CharSequence.toFloatOrNull(): Float? = screenFloatValue(this, java.lang.Float::parseFloat)


public fun CharSequence.toDoubleOrNull(): Double? = screenFloatValue(this, java.lang.Double::parseDouble)

public inline fun CharSequence.toBigInteger(): java.math.BigInteger =
		java.math.BigInteger(this.toString())

public inline fun CharSequence.toBigInteger(radix: Int): java.math.BigInteger =
		java.math.BigInteger(this.toString(), checkRadix(radix))

public fun CharSequence.toBigIntegerOrNull(): java.math.BigInteger? = toBigIntegerOrNull(10)

public fun CharSequence.toBigIntegerOrNull(radix: Int): java.math.BigInteger? {
	checkRadix(radix)
	val length = this.length
	when (length) {
		0 -> return null
		1 -> if (digitOf(this[0], radix) < 0) return null
		else -> {
			val start = if (this[0] == '-') 1 else 0
			for (index in start until length) {
				if (digitOf(this[index], radix) < 0)
					return null
			}
		}
	}
	return toBigInteger(radix)
}

public inline fun CharSequence.toBigDecimal(mathContext: java.math.MathContext): java.math.BigDecimal =
		java.math.BigDecimal(this.toString(), mathContext)

public fun CharSequence.toBigDecimalOrNull(): java.math.BigDecimal? =
		screenFloatValue(this) { it.toBigDecimal() }

public fun CharSequence.toBigDecimalOrNull(mathContext: java.math.MathContext): java.math.BigDecimal? =
		screenFloatValue(this) { it.toBigDecimal(mathContext) }

private object ScreenFloatValueRegEx {
	@JvmField val value = run {
		val Digits = "(\\p{Digit}+)"
		val HexDigits = "(\\p{XDigit}+)"
		val Exp = "[eE][+-]?$Digits"

		val HexString = "(0[xX]$HexDigits(\\.)?)|" + // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
				"(0[xX]$HexDigits?(\\.)$HexDigits)"  // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt

		val Number = "($Digits(\\.)?($Digits?)($Exp)?)|" +  // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
				"(\\.($Digits)($Exp)?)|" +                  // . Digits ExponentPart_opt FloatTypeSuffix_opt
				"(($HexString)[pP][+-]?$Digits)"            // HexSignificand BinaryExponent

		val fpRegex = "[\\x00-\\x20]*[+-]?(NaN|Infinity|(($Number)[fFdD]?))[\\x00-\\x20]*"

		Regex(fpRegex)
	}
}

private fun <T> screenFloatValue(str: CharSequence, parse: (String) -> T): T? {
	return try {
		if (ScreenFloatValueRegEx.value.matches(str))
			parse(str.toString())
		else
			null
	} catch (e: NumberFormatException) {  // overflow
		null
	}
}

@PublishedApi
internal fun checkRadix(radix: Int): Int {
	if (radix !in Character.MIN_RADIX..Character.MAX_RADIX) {
		throw IllegalArgumentException("radix $radix was not in valid range ${Character.MIN_RADIX..Character.MAX_RADIX}")
	}
	return radix
}

internal fun digitOf(char: Char, radix: Int): Int = Character.digit(char.toInt(), radix)

/*******************************/


public fun CharSequence.toByteOrNull(): Byte? = toByteOrNull(radix = 10)

/**
 * Parses the string as a signed [Byte] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 *
 * @throws IllegalArgumentException when [radix] is not a valid radix for string to number conversion.
 */
@SinceKotlin("1.1")
public fun CharSequence.toByteOrNull(radix: Int): Byte? {
	val int = this.toIntOrNull(radix) ?: return null
	if (int < Byte.MIN_VALUE || int > Byte.MAX_VALUE) return null
	return int.toByte()
}

/**
 * Parses the string as a [Short] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 */
@SinceKotlin("1.1")
public fun CharSequence.toShortOrNull(): Short? = toShortOrNull(radix = 10)

/**
 * Parses the string as a [Short] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 *
 * @throws IllegalArgumentException when [radix] is not a valid radix for string to number conversion.
 */
@SinceKotlin("1.1")
public fun CharSequence.toShortOrNull(radix: Int): Short? {
	val int = this.toIntOrNull(radix) ?: return null
	if (int < Short.MIN_VALUE || int > Short.MAX_VALUE) return null
	return int.toShort()
}

/**
 * Parses the string as an [Int] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 */
@SinceKotlin("1.1")
public fun CharSequence.toIntOrNull(): Int? = toIntOrNull(radix = 10)

/**
 * Parses the string as an [Int] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 *
 * @throws IllegalArgumentException when [radix] is not a valid radix for string to number conversion.
 */
@SinceKotlin("1.1")
public fun CharSequence.toIntOrNull(radix: Int): Int? {
	checkRadix(radix)

	val length = this.length
	if (length == 0) return null

	val start: Int
	val isNegative: Boolean
	val limit: Int

	val firstChar = this[0]
	if (firstChar < '0') {  // Possible leading sign
		if (length == 1) return null  // non-digit (possible sign) only, no digits after

		start = 1

		if (firstChar == '-') {
			isNegative = true
			limit = Int.MIN_VALUE
		} else if (firstChar == '+') {
			isNegative = false
			limit = -Int.MAX_VALUE
		} else
			return null
	} else {
		start = 0
		isNegative = false
		limit = -Int.MAX_VALUE
	}


	val limitForMaxRadix = (-Int.MAX_VALUE) / 36

	var limitBeforeMul = limitForMaxRadix
	var result = 0
	for (i in start until length) {
		val digit = digitOf(this[i], radix)

		if (digit < 0) return null
		if (result < limitBeforeMul) {
			if (limitBeforeMul == limitForMaxRadix) {
				limitBeforeMul = limit / radix

				if (result < limitBeforeMul) {
					return null
				}
			} else {
				return null
			}
		}

		result *= radix

		if (result < limit + digit) return null

		result -= digit
	}

	return if (isNegative) result else -result
}

/**
 * Parses the string as a [Long] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 */
@SinceKotlin("1.1")
public fun CharSequence.toLongOrNull(): Long? = toLongOrNull(radix = 10)

/**
 * Parses the string as a [Long] number and returns the result
 * or `null` if the string is not a valid representation of a number.
 *
 * @throws IllegalArgumentException when [radix] is not a valid radix for string to number conversion.
 */
@SinceKotlin("1.1")
public fun CharSequence.toLongOrNull(radix: Int): Long? {
	checkRadix(radix)

	val length = this.length
	if (length == 0) return null

	val start: Int
	val isNegative: Boolean
	val limit: Long

	val firstChar = this[0]
	if (firstChar < '0') {  // Possible leading sign
		if (length == 1) return null  // non-digit (possible sign) only, no digits after

		start = 1

		if (firstChar == '-') {
			isNegative = true
			limit = Long.MIN_VALUE
		} else if (firstChar == '+') {
			isNegative = false
			limit = -Long.MAX_VALUE
		} else
			return null
	} else {
		start = 0
		isNegative = false
		limit = -Long.MAX_VALUE
	}


	val limitForMaxRadix = (-Long.MAX_VALUE) / 36

	var limitBeforeMul = limitForMaxRadix
	var result = 0L
	for (i in start until length) {
		val digit = digitOf(this[i], radix)

		if (digit < 0) return null
		if (result < limitBeforeMul) {
			if (limitBeforeMul == limitForMaxRadix) {
				limitBeforeMul = limit / radix

				if (result < limitBeforeMul) {
					return null
				}
			} else {
				return null
			}
		}

		result *= radix

		if (result < limit + digit) return null

		result -= digit
	}

	return if (isNegative) result else -result
}