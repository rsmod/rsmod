package org.rsmod.api.weapons.scripts

import jakarta.inject.Inject
import org.rsmod.api.weapons.WeaponAttackManager
import org.rsmod.api.weapons.WeaponMap
import org.rsmod.api.weapons.WeaponRepository
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class WeaponScript
@Inject
constructor(
    private val repo: WeaponRepository,
    private val manager: WeaponAttackManager,
    private val weapons: Set<WeaponMap>,
) : PluginScript() {
    override fun ScriptContext.startup() {
        weapons.registerAll()
    }

    private fun Iterable<WeaponMap>.registerAll() {
        for (weapons in this) {
            weapons.register()
        }
    }

    private fun WeaponMap.register() = repo.register(manager)
}
