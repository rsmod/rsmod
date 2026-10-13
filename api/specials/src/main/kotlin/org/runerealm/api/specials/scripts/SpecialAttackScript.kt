package org.runerealm.api.specials.scripts

import jakarta.inject.Inject
import org.runerealm.api.specials.SpecialAttackManager
import org.runerealm.api.specials.SpecialAttackMap
import org.runerealm.api.specials.SpecialAttackRepository
import org.runerealm.api.specials.weapon.SpecialAttackWeapons
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

internal class SpecialAttackScript
@Inject
constructor(
    private val repo: SpecialAttackRepository,
    private val manager: SpecialAttackManager,
    private val weapons: SpecialAttackWeapons,
    private val specials: Set<SpecialAttackMap>,
) : PluginScript() {
    override fun ScriptContext.startup() {
        weapons.startup()
        specials.registerAll()
    }

    private fun Iterable<SpecialAttackMap>.registerAll() {
        for (specials in this) {
            specials.register()
        }
    }

    private fun SpecialAttackMap.register() = repo.register(manager)
}
