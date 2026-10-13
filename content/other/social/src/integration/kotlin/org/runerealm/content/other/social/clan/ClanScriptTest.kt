package org.runerealm.content.other.social.clan

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.players.OpPlayer
import net.rsprot.protocol.game.incoming.players.OpPlayerT
import net.rsprot.protocol.game.outgoing.clan.ClanChannelFull
import net.rsprot.protocol.game.outgoing.clan.ClanSettingsFull
import net.rsprot.protocol.game.outgoing.clan.MessageClanChannel
import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import net.rsprot.protocol.util.CombinedId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.config.refs.params
import org.runerealm.api.net.rsprot.handlers.OpPlayerHandler
import org.runerealm.api.net.rsprot.handlers.OpPlayerTHandler
import org.runerealm.api.player.events.social.ClanChatMessage
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.capture.CaptureClient
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.content.other.social.ChannelsTabScript
import org.runerealm.content.other.social.channels_interfaces
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.SessionStateEvent
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.obj.Wearpos
import org.runerealm.map.CoordGrid

class ClanScriptTest {
    @Test
    fun GameTestState.`five players form a clan at the clerk and chat in it`() =
        runClanTest { deps ->
            val start = CoordGrid(0, 50, 50, 20, 20)
            allocZoneCollision(start)
            player.placeAt(start)
            player.displayName = "Alice"
            player.ifOpenOverlay(interfaces.side_channels, components.toplevel_target_side7)

            val founders =
                listOf(
                        start.translateX(-1),
                        start.translateZ(1),
                        start.translateZ(-1),
                        start.translate(-1, 1),
                    )
                    .mapIndexed { index, coords ->
                        registerPlayer(coords).apply { displayName = "Founder$index" }
                    }

            talkToClerkAndForm(start.translateX(1))
            val formation = checkNotNull(deps.registry.formationOf(player))
            assertTrue(player.ui.containsOverlay(channels_interfaces.clans_creation_sidepanel))

            player.ifButton(clan_components.creation_name)
            advance()
            player.resumeStringDialog("Lumby Lads")
            advance()
            assertMessageSent("The name Lumby Lads is available.")
            assertEquals("Lumby Lads", formation.name)

            player.ifButton(clan_components.creation_confirm)
            advance()
            assertMessageSent("You need 5 founding members, including yourself, to form a clan.")

            for (founder in founders) {
                player.opPlayerT(deps, founder, clan_components.creation_invite)
                advance(2)
                founder.resumePauseButton(components.chatmenu_pbutton, sub = 1)
                advance()
            }
            assertEquals(5, formation.members.size)

            player.ifButton(clan_components.creation_confirm)
            advance()
            val clan = checkNotNull(deps.registry.clanOf(player))
            assertNull(deps.registry.formationOf(player))
            assertEquals("Lumby Lads", clan.name)
            assertEquals(ClanRanks.OWNER, clan.member(player.characterId)?.rank)
            assertEquals(ClanRanks.ADMINISTRATOR, clan.member(founders[0].characterId)?.rank)
            assertEquals(5, clan.channel.size)
            assertTrue(player.ui.containsOverlay(channels_interfaces.clans_sidepanel))
            val settings = player.captureClient.filterIsInstance<ClanSettingsFull>().last()
            assertEquals("Lumby Lads", (settings.update as ClanSettingsFull.JoinUpdate).clanName)

            player.captureClient.clearOutgoing()
            eventBus.publish(ClanChatMessage(founders[0], "hello clan", guest = false))
            val message = player.captureClient.filterIsInstance<MessageClanChannel>().single()
            assertEquals("hello clan", message.message)
            assertEquals("Founder0", message.name)

            // The tab's Chat/Leave button leaves and rejoins the clan's channel.
            player.ifButton(clan_components.sidepanel_chat)
            advance()
            assertTrue(player !in clan.channel)
            player.ifButton(clan_components.sidepanel_chat)
            advance()
            assertTrue(player in clan.channel)
        }

    @Test
    fun GameTestState.`the clan name must be unique`() = runClanTest { deps ->
        deps.registry.add(Clan(1, "Taken", 0))
        player.ifOpenOverlay(interfaces.side_channels, components.toplevel_target_side7)
        val start = CoordGrid(0, 50, 50, 20, 20)
        allocZoneCollision(start)
        player.placeAt(start)
        talkToClerkAndForm(start.translateX(1))

        player.ifButton(clan_components.creation_name)
        advance()
        player.resumeStringDialog("taken")
        advance()
        assertMessageSent("The name taken is not available.")

        player.ifButton(clan_components.creation_cancel)
        advance()
        assertNull(deps.registry.formationOf(player))
        assertMessageSent("The clan formation has been cancelled.")
    }

    @Test
    fun GameTestState.`members get their clan on login`() = runClanTest { deps ->
        player.displayName = "Alice"
        val clan = Clan(7, "Saved", 0)
        clan.members += ClanMember(player.characterId, "Alice", ClanRanks.OWNER, 0)
        deps.registry.add(clan)
        player.captureClient.clearOutgoing()

        eventBus.publish(SessionStateEvent.Login(player))

        val settings = player.captureClient.filterIsInstance<ClanSettingsFull>().single()
        assertEquals("Saved", (settings.update as ClanSettingsFull.JoinUpdate).clanName)
        val channel = player.captureClient.filterIsInstance<ClanChannelFull>().single()
        val join = channel.update as ClanChannelFull.JoinUpdate
        assertEquals(listOf("Alice"), join.members.map { it.name })
        assertEquals(ClanRanks.OWNER, join.members.single().rank)
    }

    @Test
    fun GameTestState.`a vexillum recruits players into the clan`() = runClanTest { deps ->
        val vexillum = objTypes[clan_objs.vexillum]
        assertEquals("Recruit", vexillum.paramOrNull(params.player_op5_text))

        val start = CoordGrid(0, 50, 50, 20, 20)
        allocZoneCollision(start)
        player.placeAt(start)
        player.displayName = "Alice"
        val clan = Clan(3, "Recruiters", 0)
        clan.members += ClanMember(player.characterId, "Alice", ClanRanks.OWNER, 0)
        deps.registry.add(clan)
        player.worn[Wearpos.RightHand.slot] = InvObj(clan_objs.vexillum)

        val bob = registerPlayer(start.translateX(1))
        bob.displayName = "Bob"
        player.captureClient.queue(deps.opPlayer, OpPlayer(bob.slotId, false, 5))
        advanceUntil({ player.messageSent("Bob is not accepting aid.") }, timeoutTicks = 5)

        bob.setVarBit(clan_varbits.accept_aid, 1)
        player.captureClient.queue(deps.opPlayer, OpPlayer(bob.slotId, false, 5))
        advanceUntil({ bob.ui.containsModal(interfaces.chatmenu) }, timeoutTicks = 5)
        bob.resumePauseButton(components.chatmenu_pbutton, sub = 1)
        advance()
        assertEquals(ClanRanks.RECRUIT, clan.member(bob.characterId)?.rank)
        assertTrue(bob in clan.channel)
        assertMessageSent("You are now a member of Recruiters.", bob)
    }

    @Test
    fun GameTestState.`clan chat needs a clan`() = runClanTest { _ ->
        eventBus.publish(ClanChatMessage(player, "hi", guest = false))
        assertMessageSent("You are not in a clan.")
    }

    /** Talks to a Clerk spawned at [coords] and picks "I'd like to form a clan." */
    private fun GameTestScope.talkToClerkAndForm(coords: CoordGrid) {
        val clerk = spawnNpc(coords, npcTypes[clan_npcs.clerk])
        player.opNpc1(clerk)
        var chose = false
        repeat(20) {
            advance()
            when {
                player.ui.containsModal(interfaces.chatmenu) && !chose -> {
                    player.resumePauseButton(components.chatmenu_pbutton, sub = 2)
                    chose = true
                }
                player.ui.containsModal(interfaces.chat_left) ->
                    player.resumePauseButton(components.chat_left_pbutton)
                player.ui.containsModal(interfaces.chat_right) ->
                    player.resumePauseButton(components.chat_right_pbutton)
            }
        }
    }

    private fun Player.opPlayerT(
        deps: ClanTestDependencies,
        target: Player,
        component: org.runerealm.game.type.comp.ComponentType,
    ) {
        val selected = CombinedId(component.interfaceId, component.component)
        captureClient.queue(deps.opPlayerT, OpPlayerT(target.slotId, false, selected, -1, -1))
    }

    private val Player.captureClient: CaptureClient
        get() = client as CaptureClient

    private fun Player.messageSent(text: String): Boolean =
        captureClient.filterIsInstance<MessageGame>().any { it.message == text }

    private fun GameTestState.runClanTest(body: GameTestScope.(ClanTestDependencies) -> Unit) =
        runInjectedGameTest(
            ClanTestDependencies::class,
            null,
            ClanScript::class,
            ChannelsTabScript::class,
        ) { deps ->
            deps.registry.reset()
            body(deps)
        }
}

class ClanTestDependencies
@Inject
constructor(
    val registry: ClanRegistry,
    val opPlayer: OpPlayerHandler,
    val opPlayerT: OpPlayerTHandler,
)
