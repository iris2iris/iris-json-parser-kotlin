package iris.sequence

import java.io.Reader
import java.io.StringReader
import kotlin.math.min
import kotlin.math.round

/**
 * @created 22.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class SimpleStringReader(private val source: CharSequence) : Reader() {

	private var pointer = 0
	private val length = source.length
	private val strSource = source as? String
	private val buff = (source as? CharArraySource)

	override fun read(cbuf: CharArray): Int {
		val realLen = min(length - pointer, cbuf.size)
		if (realLen <= 0)
			return -1

		if (strSource != null) {
			strSource.toCharArray(cbuf, pointer, 0, pointer + realLen)
			pointer += realLen
		} else if (buff != null) {
			buff.toCharArray(cbuf, 0, pointer, realLen)
			//System.arraycopy(buff.source, buff.start + pointer, cbuf, 0, realLen)
			pointer += realLen
		} else {
			val till = pointer + realLen
			var off = 0
			while (pointer - till < 0) {
				cbuf[off++] = source[pointer++]
			}
		}
		return realLen
	}

	override fun read(cbuf: CharArray, off: Int, len: Int): Int {
		val realLen = min(length - pointer, len)
		if (realLen <= 0)
			return -1

		if (strSource != null) {
			strSource.toCharArray(cbuf, pointer, off, pointer + realLen)
			pointer += realLen
		} else if (buff != null) {
			buff.toCharArray(cbuf, off, pointer, realLen)
			//System.arraycopy(buff.source, buff.start + pointer, cbuf, off, realLen)
			pointer += realLen
		} else {
			val till = pointer + realLen
			var off = off
			while (pointer - till < 0) {
				cbuf[off++] = source[pointer++]
			}
		}
		return realLen
	}

	override fun close() {

	}
}

fun main() {
	val buff = CharArray(4*1024)
	var totalSimple = 0.0
	var totalString = 0.0
	val repeats = 100_000
	test.length
	testChars.length
	repeat(repeats) {
		val str = testStrReader(buff)
		val ch = testSimpleStrReader(buff)
		totalSimple += ch
		totalString += str
	}
	totalSimple /= 1000000.0
	totalString /= 1000000.0
	val pct = round(totalString * 100.0 / totalSimple - 100).toInt()
	println("AVG: String: ${totalString / repeats} Simple: ${totalSimple / repeats} DIFF: ${(totalString - totalSimple)} (${if (pct >= 0) "+" else ""}$pct%)")
}

val test = (0..10_000).joinToString("|а") { it.toString() }
val testChars = IrisSequenceCharArray(test.toCharArray())
fun testStrReader(buffer: CharArray): Long {
	val start = System.nanoTime()
	val str = StringReader(test)
	do {
		val am = str.read(buffer)
		if (am == -1)
			break
	} while (true)
	val end = System.nanoTime()
	return end - start
}

fun testSimpleStrReader(buffer: CharArray): Long {

	val start = System.nanoTime()
	val str = SimpleStringReader(testChars)
	do {
		val am = str.read(buffer)
		if (am == -1)
			break
	} while (true)
	val end = System.nanoTime()
	return end - start
}