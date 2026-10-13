package org.runerealm.api.cheat

import org.runerealm.game.cheat.CheatCommandMap

public fun CheatCommandMap.register(command: String, build: CheatHandlerBuilder.() -> Unit) {
    val builder = CheatHandlerBuilder(command.lowercase()).apply(build)
    val handler = builder.build()
    this[builder.command] = handler
}
