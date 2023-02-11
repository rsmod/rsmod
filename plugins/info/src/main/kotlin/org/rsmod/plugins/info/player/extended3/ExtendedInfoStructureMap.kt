package org.rsmod.plugins.info.player.extended3

public class ExtendedInfoStructureMap {

    private val structures: Array<ExtendedInfoStructure?> = arrayOfNulls(ExtendedInfo.values.size)
    private val ordered: MutableList<ExtendedInfoStructure> = mutableListOf()

    public val size: Int get() = structures.filterNotNull().size

    public operator fun get(ext: ExtendedInfo): ExtendedInfoStructure? {
        return structures[ext.index]
    }

    public operator fun set(ext: ExtendedInfo, struct: ExtendedInfoStructure) {
        // TODO: inform of any collision?
        structures[ext.index] = struct
    }

    public fun getOrComputeOrdered(): List<ExtendedInfoStructure> {
        if (ordered.isNotEmpty()) return ordered
        val structures = structures.filterNotNull()
        if (structures.isEmpty()) return emptyList()
        ordered += structures.sortedBy { it.order }
        return ordered
    }
}
