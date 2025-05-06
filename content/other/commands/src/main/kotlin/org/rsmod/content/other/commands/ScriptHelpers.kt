package org.rsmod.content.other.commands

import org.rsmod.api.cheat.CheatHandlerBuilder
import org.rsmod.api.config.refs.modlevels
import org.rsmod.api.script.onCommand
import org.rsmod.game.cheat.Cheat
import org.rsmod.plugin.scripts.ScriptContext

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
