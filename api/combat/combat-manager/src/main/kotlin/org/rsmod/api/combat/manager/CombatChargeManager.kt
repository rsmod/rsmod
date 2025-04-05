package org.rsmod.api.combat.manager

import jakarta.inject.Inject
import org.rsmod.api.obj.charges.ObjChargeManager
import org.rsmod.api.player.righthand
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.varobjbit.UnpackedVarObjBitType

public class CombatChargeManager @Inject constructor(private val manager: ObjChargeManager) {
    /**
     * Returns the number of charges on the player's weapon.
     *
     * Internally calls [ObjChargeManager.getCharges] with `obj = player.righthand`.
     */
    public fun getWeaponCharges(player: Player, varobj: UnpackedVarObjBitType): Int =
        manager.getCharges(player.righthand, varobj)

    /**
     * Attempts to reduce the charges on the player's weapon by [decrement].
     *
     * Internally calls [ObjChargeManager.reduceWornCharges] with `wearpos = Wearpos.RightHand`.
     */
    public fun attemptDetractWeapon(
        player: Player,
        varobj: UnpackedVarObjBitType,
        decrement: Int = 1,
    ): ObjChargeManager.Uncharge =
        manager.reduceWornCharges(player, Wearpos.RightHand, varobj, decrement)
}
