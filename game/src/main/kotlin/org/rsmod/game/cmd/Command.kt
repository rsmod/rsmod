package org.rsmod.game.cmd

import org.rsmod.game.model.mob.Player

data class Command(
    val description: String,
    val rank: Int,
    val execute: (CommandBlock).() -> Unit
)

data class CommandBlock(
    val player: Player,
    val args: CommandArgs
)

class CommandArgs(private val args: List<String>) : List<String> by args
