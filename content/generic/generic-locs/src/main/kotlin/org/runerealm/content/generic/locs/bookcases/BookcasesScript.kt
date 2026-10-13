package org.runerealm.content.generic.locs.bookcases

import org.runerealm.api.config.refs.content
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class BookcasesScript : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.bookcase) { search() }
    }

    private suspend fun ProtectedAccess.search() {
        arriveDelay()
        spam("You search the books...")
        delay(2)
        val message =
            random.pick(
                "None of them look very interesting.",
                "You don't find anything that you'd ever want to read.",
                "You find nothing to interest you.",
            )
        mes(message)
    }
}
