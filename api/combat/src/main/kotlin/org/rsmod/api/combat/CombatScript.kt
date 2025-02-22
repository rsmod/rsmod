package org.rsmod.api.combat

import jakarta.inject.Inject
import org.rsmod.api.combat.styles.AttackStyles
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class CombatScript @Inject constructor(private val attackStyles: AttackStyles) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        attackStyles.startUp()
    }
}
