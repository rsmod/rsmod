package gg.rsmod.game.model.item

interface ContainerKey

sealed class ContainerStack {
    object Default : ContainerStack()
    object Always : ContainerStack()
    object Never : ContainerStack()
}

class ItemContainer private constructor(
    private val items: MutableList<Item?>,
    val stack: ContainerStack
) : List<Item?> by items {

    constructor(
        capacity: Int,
        stack: ContainerStack = ContainerStack.Default
    ) : this(
        arrayOfNulls<Item?>(capacity).toMutableList(),
        stack
    )

    operator fun set(slot: Int, item: Item?) {
        items[slot] = item
    }
}

class ItemContainerMap(
    private val containers: MutableMap<ContainerKey, ItemContainer> = mutableMapOf()
) : Map<ContainerKey, ItemContainer> by containers {

    fun register(
        key: ContainerKey,
        capacity: Int,
        stack: ContainerStack = ContainerStack.Default
    ) {
        if (containsKey(key)) {
            error("Container key already registered (key=$key)")
        }
        containers[key] = ItemContainer(capacity, stack)
    }
}
