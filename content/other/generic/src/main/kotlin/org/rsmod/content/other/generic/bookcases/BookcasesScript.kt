package org.rsmod.content.other.generic.bookcases

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.player.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.spam
import org.rsmod.api.random.GameRandom
import org.rsmod.api.script.onOpLoc1
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class BookcasesScript @Inject constructor(private val random: GameRandom) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.bookcase) { search() }
    }

    private suspend fun ProtectedAccess.search() {
        arriveDelay()
        player.spam("You search the books...")
        delay(2)
        val message =
            random.pick(
                "None of them look very interesting.",
                "You don't find anything that you'd ever want to read.",
                "You find nothing to interest you.",
            )
        player.mes(message)
    }
}
