package org.rsmod.game.cmd

import org.rsmod.game.model.mob.Player
import org.rsmod.game.privilege.Privilege

data class Command(
    val description: String,
    val privileges: Set<Privilege>,
    val execute: (CommandBlock).() -> Unit
)

data class CommandBlock(
    val player: Player,
    val args: CommandArgs
)

class CommandArgs(private val args: List<String>) : List<String> by args
