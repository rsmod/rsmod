package org.rsmod.game.cmd

import org.rsmod.game.privilege.Privilege

@DslMarker
private annotation class BuilderDslMarker

@BuilderDslMarker
class CommandBuilder(
    var description: String? = null,
    private val privileges: MutableSet<Privilege> = mutableSetOf(),
    private var execute: ((CommandBlock).() -> Unit)? = null
) {

    var privilege: Privilege
        set(value) { privileges.add(value) }
        get() = privileges.first()

    fun privileges(init: (CommandPrivilegeBuilder).() -> Unit) {
        CommandPrivilegeBuilder(privileges).apply(init)
    }

    fun execute(block: (CommandBlock).() -> Unit) {
        this.execute = block
    }

    internal fun build(): Command {
        val desc = description ?: error("Command description has not been set.")
        val execute = execute ?: error("Command logic block has not been set.")
        return Command(desc, privileges, execute)
    }
}

@BuilderDslMarker
class CommandPrivilegeBuilder(
    private val privileges: MutableSet<Privilege>
) {

    operator fun Privilege.unaryMinus() {
        privileges.add(this)
    }
}
