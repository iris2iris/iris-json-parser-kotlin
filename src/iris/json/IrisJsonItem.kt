import iris.json.IrisJson
import java.lang.Appendable

abstract class IrisJsonItem(val type: IrisJson.Type) {
	abstract operator fun get(ind: Int): IrisJsonItem
	abstract operator fun get(key: String): IrisJsonItem
	abstract fun obj(): Any?
	abstract fun <A: Appendable>joinTo(buffer: A): A
}