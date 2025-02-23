package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.type.obj.ObjType

public abstract class SpecialAttackMap(ctx: Context) {
    private val energy: SpecialAttackEnergy = ctx.energy
    private val weapons: SpecialAttackWeapons = ctx.weapons

    public abstract fun SpecialAttackRepository.register()

    public fun ProtectedAccess.hasSpecialEnergy(energyInHundreds: Int): Boolean {
        return energy.hasSpecialEnergy(player, energyInHundreds)
    }

    public fun ProtectedAccess.takeSpecialEnergy(energyInHundreds: Int) {
        energy.takeSpecialEnergy(player, energyInHundreds)
    }

    public fun getSpecialEnergyRequirement(obj: ObjType): Int? = weapons.getSpecialEnergy(obj)

    public data class Context
    @Inject
    constructor(public val energy: SpecialAttackEnergy, public val weapons: SpecialAttackWeapons)
}
