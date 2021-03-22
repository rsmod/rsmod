package org.rsmod.game.cmd

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class CommandBuilder(
    var description: String? = null,
    var privilege: Int = 0,
    private var execute: ((CommandBlock).() -> Unit)? = null
) {

    fun execute(block: (CommandBlock).() -> Unit) {
        this.execute = block
    }

    internal fun build(): Command {
        val desc = description ?: error("Command description has not been set.")
        val execute = execute ?: error("Command logic block has not been set.")
        return Command(desc, privilege, execute)
    }
}
