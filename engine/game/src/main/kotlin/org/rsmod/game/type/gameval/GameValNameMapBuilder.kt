package org.rsmod.game.type.gameval

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

public class GameValNameMapBuilder {
    private val names: MutableMap<Int, MutableMap<Int, String>> = mutableMapOf()
    private val components: MutableMap<Int, String> = mutableMapOf()
    private val columns: MutableMap<Int, String> = mutableMapOf()

    public fun putNames(group: Int, names: Map<Int, String>) {
        this.names[group] = Int2ObjectOpenHashMap(names)
    }

    public fun putComponent(parentIf: Int, component: Int, name: String) {
        val packed = (parentIf shl 16) or component
        components[packed] = name
    }

    public fun putDbColumn(parentDb: Int, column: Int, name: String) {
        val packed = (parentDb shl 16) or column
        columns[packed] = name
    }

    public fun build(): GameValNameMap {
        val names = Int2ObjectOpenHashMap(names)
        val components = Int2ObjectOpenHashMap(components)
        val columns = Int2ObjectOpenHashMap(columns)
        return GameValNameMap(names = names, components = components, columns = columns)
    }
}
