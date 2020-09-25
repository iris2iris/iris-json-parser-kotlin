package iris.json.flow

import iris.json.IrisJsonItem
import java.lang.Appendable

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowArray(tokener: Tokener) : FlowItem(tokener), Iterable<IrisJsonItem> {

	private val items = mutableListOf<FlowItem>()

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append('[')
		items.joinTo(buffer)
		buffer.append(']')
		return buffer
	}

	private var isDone = false
	private var needToParse: FlowItem? = null

	override fun get(ind: Int): IrisJsonItem {
		if (isDone)
			return items[ind]
		val toAdd = ind - items.size
		if (toAdd < 0)
			return items[ind]

		val localNeed = needToParse
		if (localNeed != null) {
			localNeed.parse()
			needToParse = null
		}
		if (toAdd > 0) {
			for (i in 0 until toAdd) {
				val next = parseNext()
				if (next == null) {
					isDone = true
					break
				}
				items += next
				next.parse()
			}
		}
		val next = parseNext()
		if (next == null) {
			isDone = true
		} else {
			items += next
			needToParse = next
		}

		return items[ind]
	}

	private fun parseNext(): FlowItem? {
		val char = tokener.nextChar()
		if (char == ']') {
			isDone = true
			return null
		}
		if (char != ',')
			tokener.back()

		return JsonFlowParser.readItem(tokener)
	}

	private fun parseNextAndAdd(): FlowItem? {
		val next = parseNext()
		if (next == null) {
			isDone = true
			return null
		}
		items += next
		next.parse()
		return next
	}

	override fun parse() {
		if (isDone)
			return
		do {
			val next = parseNext() ?: break
			next.parse()
			items += next
		} while (true)
		isDone = true
	}

	override fun get(key: String): IrisJsonItem {
		val ind = key.toInt()
		return get(ind)
	}

	private var obj : Any? = null

	override fun obj(): Any? {
		return obj ?: run {
			parse()
			obj = items.map { it.obj() }
			obj
		}
	}

	override fun iterable() = this

	override fun iterator(): Iterator<IrisJsonItem> {
		return Iter()
	}

	private inner class Iter : Iterator<IrisJsonItem> {

		private var pointer = 0

		override fun hasNext(): Boolean {
			if (isDone) {
				return pointer < items.size
			}

			while (pointer >= items.size) {
				parseNextAndAdd() ?: break
			}
			return pointer < items.size
		}

		override fun next(): IrisJsonItem {
			return get(pointer++)
		}
	}
}