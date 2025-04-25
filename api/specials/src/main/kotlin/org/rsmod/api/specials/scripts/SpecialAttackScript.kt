package org.rsmod.api.specials.scripts

import jakarta.inject.Inject
import org.rsmod.api.specials.SpecialAttackManager
import org.rsmod.api.specials.SpecialAttackMap
import org.rsmod.api.specials.SpecialAttackRepository
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
