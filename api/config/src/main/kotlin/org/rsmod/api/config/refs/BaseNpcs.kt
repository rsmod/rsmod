package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

public typealias npcs = BaseNpcs

public object BaseNpcs : NpcReferences() {
    public val man_id_3106: NpcType = find("man_id_3106", 5122204378371546963)
    public val man_id_3107: NpcType = find("man_id_3107", 5122204378371546964)
    public val man_id_3108: NpcType = find("man_id_3108", 5122204378371546965)
    public val woman_id_3111: NpcType = find("woman_id_3111", 7243516389416781088)
    public val woman_id_3112: NpcType = find("woman_id_3112", 7243516389416781089)
    public val woman_id_3113: NpcType = find("woman_id_3113", 7243516389416781090)
    public val man_id_6818: NpcType = find("man_id_6818", 5122204378371550675)
}
