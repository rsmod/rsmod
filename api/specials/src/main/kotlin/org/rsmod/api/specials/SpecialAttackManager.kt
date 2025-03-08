package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType

public class SpecialAttackManager
@Inject
constructor(private val energy: SpecialAttackEnergy, private val weapons: SpecialAttackWeapons) {
    public fun setNextAttackDelay(access: ProtectedAccess, cycles: Int) {
        access.actionDelay = access.mapClock + cycles
    }

    public fun resetCombat(access: ProtectedAccess) {
        access.stopAction()
        setNextAttackDelay(access, 0)
    }

    public fun continueCombat(access: ProtectedAccess, target: Npc) {
        access.opNpc2(target)
    }

    public fun continueCombat(access: ProtectedAccess, target: Player) {
        // TODO(combat): opplayer2
    }

    public fun hasSpecialEnergy(access: ProtectedAccess, energyInHundreds: Int): Boolean {
        return energy.hasSpecialEnergy(access.player, energyInHundreds)
    }

    public fun takeSpecialEnergy(access: ProtectedAccess, energyInHundreds: Int) {
        energy.takeSpecialEnergy(access.player, energyInHundreds)
    }

    public fun getSpecialEnergyRequirement(obj: ObjType): Int? = weapons.getSpecialEnergy(obj)
}
