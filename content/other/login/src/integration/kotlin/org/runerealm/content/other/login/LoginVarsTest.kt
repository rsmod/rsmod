package org.runerealm.content.other.login

import net.rsprot.protocol.game.outgoing.varp.VarpSmall
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.varps
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.entity.player.SessionStateEvent

class LoginVarsTest {
    @Test
    fun GameTestState.`attack option settings are sent on login even when default`() =
        runGameTest(LoginScript::class) {
            check(player.vars[varps.option_attackpriority_npc] == 0)
            check(player.vars[varps.option_attackpriority] == 0)
            client.clearOutgoing()
            eventBus.publish(SessionStateEvent.EngineLogin(player))
            val sent = client.filterIsInstance<VarpSmall>()
            assertTrue(sent.any { it.id == varps.option_attackpriority_npc.id && it.value == 0 })
            assertTrue(sent.any { it.id == varps.option_attackpriority.id && it.value == 0 })
        }
}
