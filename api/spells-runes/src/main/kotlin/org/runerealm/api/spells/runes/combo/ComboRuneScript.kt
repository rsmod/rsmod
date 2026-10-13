package org.runerealm.api.spells.runes.combo

import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class ComboRuneScript
@Inject
constructor(private val repo: ComboRuneRepository, private val enumResolver: EnumTypeMapResolver) :
    PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
