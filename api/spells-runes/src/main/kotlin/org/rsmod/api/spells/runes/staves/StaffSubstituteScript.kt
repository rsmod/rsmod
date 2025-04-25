package org.rsmod.api.spells.runes.staves

import jakarta.inject.Inject
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

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
