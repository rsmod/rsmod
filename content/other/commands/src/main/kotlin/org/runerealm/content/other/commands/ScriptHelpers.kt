package org.runerealm.content.other.commands

import org.runerealm.api.cheat.CheatHandlerBuilder
import org.runerealm.api.config.refs.modlevels
import org.runerealm.api.script.onCommand
import org.runerealm.game.cheat.Cheat
import org.runerealm.plugin.scripts.ScriptContext

internal fun ScriptContext.onCommand(
    command: String,
    desc: String,
    cheat: Cheat.() -> Unit,
    init: CheatHandlerBuilder.() -> Unit = {},
) {
    onCommand(command) {
        this.modLevel = modlevels.admin
        this.desc = desc
        this.cheat(cheat)
        init()
    }
}
