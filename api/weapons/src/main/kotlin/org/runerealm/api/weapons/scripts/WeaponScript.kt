package org.runerealm.api.weapons.scripts

import jakarta.inject.Inject
import org.runerealm.api.weapons.WeaponAttackManager
import org.runerealm.api.weapons.WeaponMap
import org.runerealm.api.weapons.WeaponRepository
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
