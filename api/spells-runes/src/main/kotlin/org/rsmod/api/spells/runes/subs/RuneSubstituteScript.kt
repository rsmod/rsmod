package org.rsmod.api.spells.runes.subs

import jakarta.inject.Inject
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class RuneSubstituteScript
@Inject
constructor(
    private val repo: RuneSubstituteRepository,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        repo.init(enumResolver)
    }
}
