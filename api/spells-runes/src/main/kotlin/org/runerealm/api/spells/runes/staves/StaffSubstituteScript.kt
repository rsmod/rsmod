package org.runerealm.api.spells.runes.staves

import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class StaffSubstituteScript
@Inject
constructor(
    private val repo: StaffSubstituteRepository,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
