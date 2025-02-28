package org.rsmod.api.combat.weapon.scripts

import jakarta.inject.Inject
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal class AttackStylesScript @Inject constructor(private val attackStyles: AttackStyles) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        attackStyles.startUp()
    }
}
