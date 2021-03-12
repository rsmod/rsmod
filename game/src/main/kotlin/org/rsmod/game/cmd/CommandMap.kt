package org.rsmod.game.cmd

import com.github.michaelbull.logging.InlineLogger

private val logger = InlineLogger()

class CommandMap(
    private val commands: MutableMap<String, Command> = mutableMapOf()
) : Map<String, Command> by commands {

    fun register(name: String, init: CommandBuilder.() -> Unit) {
        val builder = CommandBuilder().apply(init)
        val lowercase = name.toLowerCase()
        if (commands.containsKey(lowercase)) {
            error("Command with name has already been registered (cmd=$name)")
        }
        val cmd = builder.build()
        logger.trace { "Register command (cmd=$name, desc=${cmd.description})" }
        commands[lowercase] = cmd
    }
}
