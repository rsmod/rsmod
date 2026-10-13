package org.runerealm.api.spells.runes.subs

import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class RuneSubstituteScript
@Inject
constructor(
    private val repo: RuneSubstituteRepository,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
