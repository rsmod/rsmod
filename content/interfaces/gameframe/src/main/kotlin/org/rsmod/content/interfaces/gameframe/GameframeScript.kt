package org.rsmod.content.interfaces.gameframe

import jakarta.inject.Inject
import org.rsmod.api.player.chatboxUnlocked
import org.rsmod.api.player.events.SessionStateEvent
import org.rsmod.api.script.onPlayerInit
import org.rsmod.content.interfaces.gameframe.impl.SidePanelsResizablePane
import org.rsmod.content.interfaces.gameframe.util.openGameframe
import org.rsmod.events.EventBus
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.plugin.scripts.SimplePluginScript

class GameframeScript
@Inject
constructor(private val sidePanelsResizable: SidePanelsResizablePane) : SimplePluginScript() {
    override fun ScriptContext.startUp() {
        onPlayerInit { trigger(this, eventBus) }
    }

    private fun trigger(event: SessionStateEvent.Initialize, eventBus: EventBus) =
        with(event) {
            player.openGameframe(eventBus, sidePanelsResizable)
            player.chatboxUnlocked = true
        }
}
