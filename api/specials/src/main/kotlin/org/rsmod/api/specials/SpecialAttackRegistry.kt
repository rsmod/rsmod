package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.ObjType

public class SpecialAttackRegistry @Inject constructor(private val weapons: SpecialAttackWeapons) {
    private val specials: MutableMap<Int, SpecialAttack> = mutableMapOf()

    public operator fun get(obj: InvObj): SpecialAttack? = specials[obj.id]

    public fun add(obj: ObjType, spec: InstantSpecialAttack): Result.Add {
        if (obj.id in specials) {
            return Result.Add.AlreadyAdded
        }
        val energy = weapons.getSpecialEnergy(obj) ?: return Result.Add.SpecialEnergyNotMapped
        val special = SpecialAttack.Instant(energy, spec)
        specials[obj.id] = special
        return Result.Add.Success
    }

    public fun add(obj: ObjType, spec: MeleeSpecialAttack): Result.Add {
        if (obj.id in specials) {
            return Result.Add.AlreadyAdded
        }
        val energy = weapons.getSpecialEnergy(obj) ?: return Result.Add.SpecialEnergyNotMapped
        val special = SpecialAttack.Melee(energy, spec)
        specials[obj.id] = special
        return Result.Add.Success
    }

    public fun add(obj: ObjType, spec: RangedSpecialAttack): Result.Add {
        if (obj.id in specials) {
            return Result.Add.AlreadyAdded
        }
        val energy = weapons.getSpecialEnergy(obj) ?: return Result.Add.SpecialEnergyNotMapped
        val special = SpecialAttack.Ranged(energy, spec)
        specials[obj.id] = special
        return Result.Add.Success
    }

    public class Result {
        public sealed class Add {
            public data object Success : Add()

            public sealed class Failure : Add()

            public data object AlreadyAdded : Add()

            public data object SpecialEnergyNotMapped : Add()
        }
    }
}
