package org.rsmod.api.weapons

import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.ObjType

public class WeaponRegistry {
    private val weapons = hashMapOf<Int, Weapon<*>>()

    private operator fun get(obj: InvObj?): Weapon<*>? = weapons[obj?.id]

    public fun getMelee(obj: InvObj?): MeleeWeapon? {
        val weapon = this[obj] ?: return null
        return weapon as? MeleeWeapon
    }

    public fun getRanged(obj: InvObj): RangedWeapon? {
        val weapon = this[obj] ?: return null
        return weapon as? RangedWeapon
    }

    public fun getMagic(obj: InvObj): MagicWeapon? {
        val weapon = this[obj] ?: return null
        return weapon as? MagicWeapon
    }

    public fun add(obj: ObjType, weapon: Weapon<*>): Result.Add {
        if (obj.id in weapons) {
            return Result.Add.AlreadyAdded
        }
        weapons[obj.id] = weapon
        return Result.Add.Success
    }

    public fun remove(obj: ObjType) {
        weapons.remove(obj.id)
    }

    public class Result {
        public sealed class Add {
            public data object Success : Add()

            public sealed class Failure : Add()

            public data object AlreadyAdded : Failure()
        }
    }
}
