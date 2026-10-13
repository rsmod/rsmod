package org.runerealm.api.spells.runes.fake

import jakarta.inject.Inject
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

public class FakeRuneScript
@Inject
constructor(private val repo: FakeRuneRepository, private val enumResolver: EnumTypeMapResolver) :
    PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}
