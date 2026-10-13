package org.runerealm.api.spells.runes.compact

import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class CompactRuneScript
@Inject
constructor(
    private val repo: CompactRuneRepository,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
