package org.rsmod.api.spells.runes.combo

import jakarta.inject.Inject
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class ComboRuneScript
@Inject
constructor(private val repo: ComboRuneRepository, private val enumResolver: EnumTypeMapResolver) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        repo.init(enumResolver)
    }
}
