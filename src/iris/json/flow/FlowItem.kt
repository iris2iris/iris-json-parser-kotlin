package iris.json.flow

import iris.json.IrisJsonItem

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
abstract class FlowItem(val tokener: Tokener) : IrisJsonItem() {
    abstract fun parse()
}