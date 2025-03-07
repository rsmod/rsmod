package org.rsmod.api.combat.magic.autocast

import jakarta.inject.Inject
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class AutocastScript @Inject constructor(private val spells: AutocastSpells) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        spells.startUp()
    }
}
