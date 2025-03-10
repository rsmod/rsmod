package org.rsmod.api.combat.weapon

import jakarta.inject.Inject
import kotlin.math.max
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList

public class WeaponSpeeds
@Inject
constructor(private val objTypes: ObjTypeList, private val styles: AttackStyles) {
    public fun base(player: Player): Int {
        val weapon = player.righthand ?: return constants.combat_default_attackrate
        return objTypes[weapon].param(params.attackrate)
    }

    public fun actual(player: Player, style: AttackStyle?): Int {
        val baseSpeed = base(player)
        return if (style == AttackStyle.RapidRanged) {
            max(1, baseSpeed - 1)
        } else {
            baseSpeed
        }
    }

    // Avoids needing the [AttackStyle] dependency when calling `actual` unless explicitly required.
    public fun actual(player: Player): Int = actual(player, player.currentAttackStyle())

    private fun Player.currentAttackStyle(): AttackStyle? = styles.get(this)
}
