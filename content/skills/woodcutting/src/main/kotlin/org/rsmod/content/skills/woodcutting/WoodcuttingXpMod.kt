package org.rsmod.content.skills.woodcutting

import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.feet
import org.rsmod.api.player.head
import org.rsmod.api.player.legs
import org.rsmod.api.player.torso
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.xpmod.StatXpMod
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.forestry_boots
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.forestry_hat
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.forestry_legs
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.forestry_top
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.lumberjack_boots
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.lumberjack_hat
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.lumberjack_legs
import org.rsmod.content.skills.woodcutting.WoodcuttingOutfit.lumberjack_top
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.isAnyType

class WoodcuttingXpMod : StatXpMod(stats.woodcutting) {
    override fun Player.modifier(): Double {
        var bonus = 0.0

        if (head.isAnyType(lumberjack_hat, forestry_hat)) {
            bonus += 0.04
        }

        if (torso.isAnyType(lumberjack_top, forestry_top)) {
            bonus += 0.08
        }

        if (legs.isAnyType(lumberjack_legs, forestry_legs)) {
            bonus += 0.06
        }

        if (feet.isAnyType(lumberjack_boots, forestry_boots)) {
            bonus += 0.02
        }

        return bonus
    }
}

internal object WoodcuttingOutfit : ObjReferences() {
    val lumberjack_hat = find("lumberjack_hat")
    val lumberjack_top = find("lumberjack_top")
    val lumberjack_legs = find("lumberjack_legs")
    val lumberjack_boots = find("lumberjack_boots")
    val forestry_hat = find("forestry_hat")
    val forestry_top = find("forestry_top")
    val forestry_legs = find("forestry_legs")
    val forestry_boots = find("forestry_boots")
}
