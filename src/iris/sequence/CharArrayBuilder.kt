package iris.sequence

import java.io.File
import kotlin.math.min
import kotlin.math.round

/**
 * @created 23.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class CharArrayBuilder(initialCapacity: Int = DEFAULT_CAPACITY) : Appendable, CharSequence, CharArraySource {

	companion object {
		const val DEFAULT_CAPACITY = 16
	}

	private var buffer = CharArray(initialCapacity)
	private var pointer = 0

	override val length: Int get() = pointer

	fun reset() {
		pointer = 0
	}

	override fun get(index: Int): Char {
		return buffer[index]
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		return IrisSequenceCharArray(buffer, startIndex, endIndex)
	}

	private fun ensureCapacityInternal(minimumCapacity: Int) {
		val oldCapacity: Int = buffer.size
		if (minimumCapacity > oldCapacity) {
			buffer = buffer.copyOf(newCapacity(minimumCapacity))
		}
	}

	private fun newCapacity(minCapacity: Int): Int {
		val oldCapacity: Int = buffer.size
		var newCapacity = (oldCapacity shl 1) + 2
		if (newCapacity - minCapacity < 0) {
			newCapacity = minCapacity
		}
		return newCapacity
	}

	override fun append(csq: CharSequence): CharArrayBuilder {
		return append(csq, 0, csq.length)
	}

	override fun append(csq: CharSequence, start: Int, end: Int): CharArrayBuilder {
		val len = end - start
		ensureCapacityInternal(pointer + len)
		for (i in start until end)
			buffer[pointer++] = csq[i]
		return this
	}

	fun append(csq: String): CharArrayBuilder {
		val len = csq.length
		ensureCapacityInternal(pointer + len)
		csq.toCharArray(buffer, pointer)
		pointer += len
		return this
	}

	fun append(csq: String, start: Int = 0, end: Int = csq.length): CharArrayBuilder {
		val len = end - start
		ensureCapacityInternal(pointer + len)
		csq.toCharArray(buffer, pointer, start, end)
		pointer += len
		return this
	}

	override fun append(c: Char): CharArrayBuilder {
		ensureCapacityInternal(pointer + 1)
		buffer[pointer++] = c
		return this
	}

	fun append(arr: CharArray): CharArrayBuilder {
		val len = arr.size
		ensureCapacityInternal(pointer + len)
		arr.copyInto(buffer, pointer, 0, len)
		//System.arraycopy(arr, 0, buffer, pointer, len)
		pointer += len
		return this
	}

	fun append(arr: CharArray, start: Int = 0, end: Int = arr.size): CharArrayBuilder {
		val len = end - start
		ensureCapacityInternal(pointer + len)
		arr.copyInto(buffer, pointer, start, end)
		//System.arraycopy(arr, start, buffer, pointer, len)
		pointer += len
		return this
	}

	/*fun toCharArray(dest: CharArray = CharArray(length), start: Int = 0, len: Int = dest.size): CharArray {
		val realLen = min(len, length)
		if (realLen == 0)
			return CharArray(0)
		System.arraycopy(buffer, 0, dest, start, realLen)
		return dest
	}*/

	override fun toCharArray(): CharArray {
		val len = length
		return toCharArray(CharArray(len), 0, 0, len)
	}

	override fun toCharArray(start: Int, len: Int): CharArray {
		return toCharArray(CharArray(len), 0, start, len)
	}

	override fun toCharArray(dest: CharArray): CharArray {
		val len = min(length, dest.size)
		return toCharArray(dest, 0, 0, len)
	}

	override fun toCharArray(dest: CharArray, destOffset: Int, start: Int, len: Int): CharArray {
		val realLen = min(len, length)
		if (realLen == 0)
			return CharArray(0)
		buffer.copyInto(dest, destOffset, start, start + realLen)
		//System.arraycopy(buffer, 0, dest, start, realLen)
		return dest
	}

	override fun toString(): String {
		return String(buffer, 0, pointer)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CharSequence) return false
		if (length != other.length) return false
		for (i in 0 until length)
			if (buffer[i] != other[i])
				return false
		return true
	}

	override fun hashCode(): Int {
		val l = buffer.size
		return l + if (l > 0) get(0).toInt()*1023 else 0
	}
}

fun main() {
	var totalChar = 0.0
	var totalString = 0.0
	val repeats = 100_000
	repeat(repeats) {
		val str = testStringBuilder()
		val ch = testCharBuilder()
		totalChar += ch
		totalString += str
	}
	totalChar /= 1000000.0
	totalString /= 1000000.0
	val pct = round(totalString * 100.0 / totalChar - 100).toInt()
	println("AVG: String: ${totalString / repeats} Char: ${totalChar / repeats} DIFF: ${(totalString - totalChar)} (${if (pct >= 0) "+" else ""}$pct%)")
}

//val strings = (0..1000).map { "аааа|$it" } // strings
//val strings = (0..1000).joinToString { "а|$it" }.chunked(1).map { it.first() } // char
//val strings = (0..1000).joinToString { "а|$it" }.chunked(4*1024) // long strings
//val strings = File("test_array.json").readText().chunked(4*1024) // long strings
val strings = File("test_array.json").readText().chunked(4*1024).map { it.toCharArray() } // long chars

fun testCharBuilder(): Long {
	val start = System.nanoTime()
	val sb = CharArrayBuilder()
	for (i in strings)
		sb.append(i)
	//val d = sb.toString()
	val end = System.nanoTime()
	/*if (d == "111")
		println("111")*/
	return end - start
}

fun testStringBuilder(): Long {
	val start = System.nanoTime()
	val sb = StringBuilder()
	for (i in strings)
		sb.append(i)
	//val d = sb.toString()
	val end = System.nanoTime()
	/*if (d == "111")
		println("111")*/
	return end - start
}