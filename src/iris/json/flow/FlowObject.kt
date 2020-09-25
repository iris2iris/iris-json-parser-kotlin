package iris.json.flow

import iris.json.IrisJsonItem
import iris.json.IrisJsonNull
import java.lang.Appendable

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class FlowObject(tokener: Tokener) : FlowItem(tokener) {

    class Entry(val key: CharSequence, val value: FlowItem) {
        override fun toString(): String {
            return "\"$key\": $value"
        }
    }

    override fun get(ind: Int): IrisJsonItem {
        return get(ind.toString())
    }

    private val entries = mutableListOf<Entry>()

    private var isDone = false
    private var needToParse: FlowItem? = null

    override fun get(key: String): IrisJsonItem {
        for (e in entries) {
            if (e.key == key)
                return e.value
        }
        if (isDone) return IrisJsonNull.Null
        val needToParse = this.needToParse
        if (needToParse != null) {
            needToParse.parse()
            this.needToParse = null
        }
        do {
            val next = parseNext() ?: break
            entries += next
            if (next.key == key) {
                this.needToParse = next.value
                return next.value
            }
            next.value.parse()
        } while (true)

        isDone = true
        return IrisJsonNull.Null
    }

    private fun parseNext() : Entry? {
        var char = tokener.nextChar()
        if (char == '}') {
            isDone = true
            return null
        }
        if (char == ',') {
            char = tokener.nextChar()
        }
        if (!(char == '"' || char == '\''))
            throw tokener.exception("\" (quote) or \"'\" was expected")

        val key = tokener.readString(char)
        char = tokener.nextChar()
        if (char != ':')
            throw tokener.exception("\":\" was expected")
        val value = JsonFlowParser.readItem(tokener)
        return Entry(key, value)
    }

    private var obj: Any? = null

    override fun obj(): Any? {
        if (obj != null)
            return obj
        parse()
        val res = mutableMapOf<String, Any?>()
        for (it in entries )
            res[it.key.toString()] = it.value.obj()
        obj = res
        return res
    }

    override fun parse() {
        if (isDone) return
        do {
            val next = parseNext() ?: break
            entries += next
            next.value.parse()
        } while (true)

        isDone = true
    }

    override fun <A : Appendable> joinTo(buffer: A): A {
        parse()
        buffer.append("{")
        var firstDone = false
        for (entry in entries) {
            if (firstDone)
                buffer.append(", ")
            else
                firstDone = true
            buffer.append("\"")
            buffer.append(entry.key)
            buffer.append("\": ")
            entry.value.joinTo(buffer)

        }
        buffer.append('}')
        return buffer
    }
}