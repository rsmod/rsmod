package org.rsmod.game.cheat

import org.rsmod.game.entity.Player

public class CheatCommandMap {
    public val commands: MutableMap<String, CheatHandler> = hashMapOf()

    public fun execute(player: Player, command: String, args: List<String>): Boolean {
        val handler = this[command] ?: return false
        val cheat = Cheat(player, command, args)
        handler.action(cheat)
        return true
    }

    public fun put(name: String, handler: CheatHandler) {
        commands[name] = handler
    }

    public operator fun set(name: String, handler: CheatHandler) {
        check(!commands.containsKey(name)) {
            "Command `$name` is already registered. " +
                "You may use the `put` function to bypass this check."
        }
        put(name, handler)
    }

    public operator fun get(name: String): CheatHandler? = commands[name]
}
