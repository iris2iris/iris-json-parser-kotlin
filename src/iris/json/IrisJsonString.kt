package iris.json

import IrisJsonItem
import java.lang.Appendable

class IrisJsonString(private val data: IrisSequence) : IrisJsonItem(IrisJson.Type.String) {
	override fun toString(): String {
		return '"' + data.toString() + '"'
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append('"')
		// TODO: To find out. Is it faster to user append(data.toString()) or append(data as CharSequence)
		buffer.append(data)
		buffer.append('"')
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	private val ready by lazy(LazyThreadSafetyMode.NONE) { init() }

	private fun init(): String {
		return data.toString().replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r")
	}

	override fun obj(): Any? {
		return ready
	}
}