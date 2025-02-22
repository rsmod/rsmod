package org.rsmod.api.combat.player

import jakarta.inject.Inject
import org.rsmod.api.combat.righthand
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

public class WeaponSpeeds @Inject constructor(private val objTypes: ObjTypeList) {
    public fun base(player: Player): Int {
        val weapon = player.righthand ?: return constants.combat_default_attackrate
        return objTypes[weapon].param(params.attackrate)
    }

    public fun actual(player: Player): Int {
        // TODO: attackrate_rapid param when using rapid style on special ranged weapons.
        return base(player)
    }
}
