package org.rsmod.api.player.ui

import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import net.rsprot.protocol.game.outgoing.misc.player.TriggerOnDialogAbort
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.net.rsprot.handlers.ResumePCountDialogHandler
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.capture.attachClientCapture
import org.rsmod.api.testing.factory.playerFactory
import org.rsmod.events.EventBus

class IfCloseTest {
    /**
     * Ensures that calling [ifClose] properly cancels the player's active script when specific
     * input dialogues (e.g., `countDialog`) are open.
     *
     * These dialogue boxes are not standard modals or overlays but are built using cs2. Unlike
     * modals, they cannot be verified in the same way as [ResumePauseButton]. As a result,
     * [ifClose] is responsible for cancelling these cs2-built dialogues and the player's active
     * coroutine (active script).
     */
    @Test
    fun GameTestState.`call during a dialog input active script`() = runBasicGameTest {
        val player = playerFactory.create()
        val client = player.attachClientCapture()
        var input: Int? = null
        player.withProtectedAccess { input = countDialog() }
        checkNotNull(player.activeCoroutine)

        player.ifClose(EventBus())
        assertTrue(client.contains(TriggerOnDialogAbort))

        val resume = ResumePCountDialog(5)
        val handler = ResumePCountDialogHandler()
        handler.handle(player, resume)

        assertNull(player.activeCoroutine)
        assertNull(input)
    }
}
