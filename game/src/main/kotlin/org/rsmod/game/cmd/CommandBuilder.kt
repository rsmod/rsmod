package org.rsmod.game.cmd

import org.rsmod.game.model.mob.Player

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class CommandBuilder(
    var description: String? = null,
    var rank: Int = 0,
    private var execute: ((Player).(CommandArgs) -> Unit)? = null
) {

    fun execute(block: (Player).(CommandArgs) -> Unit) {
        this.execute = block
    }

    internal fun build(): Command {
        val desc = description ?: error("Command description has not been set.")
        val execute = execute ?: error("Command logic has not been set.")
        return Command(desc, rank, execute)
    }
}
