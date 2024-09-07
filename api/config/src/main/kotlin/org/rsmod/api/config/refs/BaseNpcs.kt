package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.type.npc.NpcType

public typealias npcs = BaseNpcs

public object BaseNpcs : NpcReferences() {
    public val man_id_3106: NpcType = find(8735917928383944456)
    public val man_id_3107: NpcType = find(8735917928383944457)
    public val man_id_3108: NpcType = find(8735917928383944458)
    public val woman_id_3111: NpcType = find(2176214274230320079)
    public val woman_id_3112: NpcType = find(2176214274230320080)
    public val woman_id_3113: NpcType = find(2176214274230320081)
    public val man_id_6818: NpcType = find(8735917928383948168)
}
