package org.rsmod.api.spells.runes.fake

import jakarta.inject.Inject
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class FakeRuneScript
@Inject
constructor(private val repo: FakeRuneRepository, private val enumResolver: EnumTypeMapResolver) :
    PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
