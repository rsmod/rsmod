package gg.rsmod.game.cmd

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.model.mob.Player

private val logger = InlineLogger()

class CommandArgs(private val args: List<String>) : List<String> by args

data class Command(
    val description: String,
    val rank: Int,
    val execute: (Player).(CommandArgs) -> Unit
)

class CommandMap(
    private val commands: MutableMap<String, Command>
) : Map<String, Command> by commands {

    @Inject
    constructor() : this(mutableMapOf())

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
