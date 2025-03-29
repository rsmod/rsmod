package org.rsmod.api.spells.attack

import jakarta.inject.Inject
import org.rsmod.game.type.obj.ObjType

public class SpellAttackRepository @Inject constructor(private val registry: SpellAttackRegistry) {
    public fun register(spell: ObjType, attack: SpellAttack) {
        val result = registry.add(spell, attack)
        assertValidResult(spell, result)
    }

    private fun assertValidResult(spell: ObjType, result: SpellAttackRegistry.Result.Add) {
        when (result) {
            SpellAttackRegistry.Result.Add.AlreadyAdded -> error("Spell already mapped: $spell")
            SpellAttackRegistry.Result.Add.Success -> {
                /* no-op */
            }
        }
    }
}
