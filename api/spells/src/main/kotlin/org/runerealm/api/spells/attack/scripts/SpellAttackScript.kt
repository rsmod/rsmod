package org.runerealm.api.spells.attack.scripts

import jakarta.inject.Inject
import org.runerealm.api.spells.attack.SpellAttackManager
import org.runerealm.api.spells.attack.SpellAttackMap
import org.runerealm.api.spells.attack.SpellAttackRepository
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
