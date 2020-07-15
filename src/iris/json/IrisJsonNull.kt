package iris.json

open class IrisJsonNull : IrisJsonItem(IrisJson.Type.Null) {

	companion object {
		val Null = IrisJsonNull()
	}

	override fun get(ind: Int): IrisJsonItem {
		return this
	}

	override fun get(key: String): IrisJsonItem {
		return this
	}

	override fun obj(): Any? {
		return null
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		buffer.append("null")
		return buffer
	}

	override fun toString(): String {
		return "null"
	}
}