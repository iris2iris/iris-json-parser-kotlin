package iris.sequence

/**
 * @created 25.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface IrisSequence: CharSequence {
	fun <A : Appendable> joinTo(buffer: A): A
}