package org.rsmod.game.model.mob.info

public class ExtendedInfoSet(
    private val extendedInfo: MutableSet<Class<out ExtendedInfo>> = mutableSetOf()
) : MutableSet<Class<out ExtendedInfo>> by extendedInfo {

    public inline fun filter(predicate: (Class<out ExtendedInfo>) -> Boolean): ExtendedInfoSet {
        val destination = mutableSetOf<Class<out ExtendedInfo>>()
        for (element in this) {
            if (!predicate(element)) continue
            destination += element
        }
        return ExtendedInfoSet(destination)
    }

    public companion object {

        public fun of(vararg elements: Class<out ExtendedInfo>): ExtendedInfoSet {
            return ExtendedInfoSet(mutableSetOf(*elements))
        }
    }
}
