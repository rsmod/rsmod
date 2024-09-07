package org.rsmod.api.cheat

import org.rsmod.game.cheat.CheatCommandMap

public fun CheatCommandMap.register(command: String, build: CheatHandlerBuilder.() -> Unit) {
    val builder = CheatHandlerBuilder(command.lowercase()).apply(build)
    val handler = builder.build()
    this[builder.command] = handler
}
