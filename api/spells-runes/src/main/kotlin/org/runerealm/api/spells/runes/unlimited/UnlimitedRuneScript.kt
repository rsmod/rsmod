package org.runerealm.api.spells.runes.unlimited

import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class UnlimitedRuneScript
@Inject
constructor(
    private val repo: UnlimitedRuneRepository,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
