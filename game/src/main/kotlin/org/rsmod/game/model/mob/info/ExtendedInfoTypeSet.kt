package org.rsmod.game.model.mob.info

public class ExtendedInfoTypeSet(
    private val extendedInfo: HashSet<Class<out ExtendedInfo>> = HashSet()
) : MutableSet<Class<out ExtendedInfo>> by extendedInfo {

    public inline fun filter(predicate: (Class<out ExtendedInfo>) -> Boolean): ExtendedInfoTypeSet {
        val destination = HashSet<Class<out ExtendedInfo>>()
        for (element in this) {
            if (!predicate(element)) continue
            destination += element
        }
        return ExtendedInfoTypeSet(destination)
    }

    public companion object {

        public fun of(vararg elements: Class<out ExtendedInfo>): ExtendedInfoTypeSet {
            return ExtendedInfoTypeSet(elements.toCollection(HashSet()))
        }
    }
}
