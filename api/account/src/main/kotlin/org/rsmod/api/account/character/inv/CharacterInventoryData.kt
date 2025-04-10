package org.rsmod.api.account.character.inv

import org.rsmod.api.account.character.CharacterDataStage

public class CharacterInventoryData(public val inventories: List<Inventory>) :
    CharacterDataStage.Segment {
    public data class Obj(val type: Int, val count: Int, val vars: Int)

    public data class Inventory(
        val rowId: Int,
        val type: Int,
        val objs: MutableMap<Int, Obj> = mutableMapOf(),
    )
}
