package org.rsmod.api.spells.attack

import org.rsmod.game.type.obj.ObjType

public class SpellAttackRegistry {
    private val attacks = hashMapOf<Int, SpellAttack>()

    public operator fun get(spell: ObjType): SpellAttack? = attacks[spell.id]

    public fun add(spell: ObjType, attack: SpellAttack): Result.Add {
        if (spell.id in attacks) {
            return Result.Add.AlreadyAdded
        }
        attacks[spell.id] = attack
        return Result.Add.Success
    }

    public class Result {
        public sealed class Add {
            public data object Success : Add()

            public sealed class Failure : Add()

            public data object AlreadyAdded : Failure()
        }
    }
}
