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
    override fun ScriptContext.startUp() {
        weapons.startUp()
        specials.registerAll()
    }

    private fun Iterable<SpecialAttackMap>.registerAll() {
        for (specials in this) {
            specials.register()
        }
    }

    private fun SpecialAttackMap.register(): Unit = repo.register(manager)
}
