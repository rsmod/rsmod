package org.rsmod.game.type.gameval

public data class GameValNameMap(
    public val names: Map<Int, Map<Int, String>>,
    public val components: Map<Int, String>,
    public val columns: Map<Int, String>,
) {
    public operator fun get(group: Int, id: Int): String? {
        return names[group]?.get(id)
    }

    public fun getComponent(interf: Int, component: Int): String? {
        return components[(interf shl 16) or component]
    }

    public fun getColumn(table: Int, column: Int): String? {
        return columns[(table shl 16) or column]
    }
}
