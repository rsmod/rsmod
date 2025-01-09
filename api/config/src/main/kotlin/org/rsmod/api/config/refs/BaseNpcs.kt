package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

public typealias npcs = BaseNpcs

public object BaseNpcs : NpcReferences() {
    public val man_id_3106: NpcType = find("man_id_3106", 5122201184108336581)
    public val man_id_3107: NpcType = find("man_id_3107", 5122201184108336582)
    public val man_id_3108: NpcType = find("man_id_3108", 5122201184108336583)
    public val woman_id_3111: NpcType = find("woman_id_3111", 7243513195153570706)
    public val woman_id_3112: NpcType = find("woman_id_3112", 7243513195153570707)
    public val woman_id_3113: NpcType = find("woman_id_3113", 7243513195153570708)
    public val man_id_6818: NpcType = find("man_id_6818", 5122201184108340293)
}
