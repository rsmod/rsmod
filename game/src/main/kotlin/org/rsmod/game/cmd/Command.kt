package org.rsmod.game.cmd

import org.rsmod.game.model.mob.Player

data class Command(
    val description: String,
    val rank: Int,
    val execute: (Player).(CommandArgs) -> Unit
)

class CommandArgs(private val args: List<String>) : List<String> by args
