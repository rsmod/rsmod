package org.rsmod.api.spells.attack.scripts

import jakarta.inject.Inject
import org.rsmod.api.spells.attack.SpellAttackManager
import org.rsmod.api.spells.attack.SpellAttackMap
import org.rsmod.api.spells.attack.SpellAttackRepository
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class SpellAttackScript
@Inject
constructor(
    private val repo: SpellAttackRepository,
    private val manager: SpellAttackManager,
    private val attacks: Set<SpellAttackMap>,
) : PluginScript() {
    override fun ScriptContext.startup() {
        attacks.registerAll()
    }

    private fun Iterable<SpellAttackMap>.registerAll() {
        for (attack in this) {
            attack.register()
        }
    }

    private fun SpellAttackMap.register() = repo.register(manager)
}
