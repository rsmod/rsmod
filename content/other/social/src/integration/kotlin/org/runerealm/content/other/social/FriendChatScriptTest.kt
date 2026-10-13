package org.runerealm.content.other.social

import net.rsprot.protocol.game.outgoing.friendchat.MessageFriendChannel
import net.rsprot.protocol.game.outgoing.friendchat.UpdateFriendChatChannelFullV2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.player.events.social.FriendChatJoinLeave
import org.runerealm.api.player.events.social.FriendChatKick
import org.runerealm.api.player.events.social.FriendChatMessage
import org.runerealm.api.player.events.social.FriendChatSetRank
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.capture.CaptureClient
import org.runerealm.game.entity.player.FriendChatSettings
import org.runerealm.game.type.interf.IfButtonOp

class FriendChatScriptTest {
    @Test
    fun GameTestState.`join a channel and talk in it`() =
        runGameTest(FriendChatScript::class) {
            player.displayName = "Alice"
            player.friendChat = player.friendChat.copy(name = "Alice chat")
            val bob = registerPlayer()
            bob.displayName = "Bob"
            val aliceClient = player.client as CaptureClient
            val bobClient = bob.client as CaptureClient

            eventBus.publish(FriendChatJoinLeave(player, "alice"))
            eventBus.publish(FriendChatJoinLeave(bob, "Alice"))
            val join = bobClient.filterIsInstance<UpdateFriendChatChannelFullV2>().single()
            val update = join.updateType as UpdateFriendChatChannelFullV2.JoinUpdate
            // Channel names travel base-37 encoded, which the client capitalises for display.
            assertEquals("alice_chat", update.channelName)
            assertMessageSent("Now talking in chat-channel Alice chat", bob)

            aliceClient.clearOutgoing()
            bobClient.clearOutgoing()
            eventBus.publish(FriendChatMessage(bob, "hello"))
            assertEquals(
                "hello",
                aliceClient.filterIsInstance<MessageFriendChannel>().single().message,
            )
            assertEquals("Bob", bobClient.filterIsInstance<MessageFriendChannel>().single().sender)

            aliceClient.clearOutgoing()
            eventBus.publish(FriendChatJoinLeave(bob, null))
            eventBus.publish(FriendChatMessage(player, "anyone?"))
            assertTrue(
                bobClient.filterIsInstance<MessageFriendChannel>().none { it.message == "anyone?" }
            )
        }

    @Test
    fun GameTestState.`a channel that was never set up does not exist`() =
        runGameTest(FriendChatScript::class) {
            player.displayName = "Alice"
            val bob = registerPlayer()
            bob.displayName = "Bob"
            eventBus.publish(FriendChatJoinLeave(player, "Bob"))
            eventBus.publish(FriendChatJoinLeave(player, "Nobody"))
            val client = player.client as CaptureClient
            assertTrue(client.filterIsInstance<UpdateFriendChatChannelFullV2>().isEmpty())
            assertMessageSent("The channel you tried to join does not exist.")
        }

    @Test
    fun GameTestState.`setup interface names the channel and sets who may enter`() =
        runGameTest(FriendChatScript::class) {
            player.displayName = "Alice"
            player.ifOpenOverlay(
                channels_interfaces.chatchannel_current,
                components.toplevel_target_side7,
            )
            player.ifButton(friendchat_components.current_setup)
            advance()
            assertModalOpen(friendchat_interfaces.setup)

            player.ifButton(friendchat_components.setup_prefix, op = IfButtonOp.Op1)
            advance()
            player.resumeStringDialog("Alices Place")
            advance()
            assertEquals("Alices Place", player.friendChat.name)

            assertModalOpen(friendchat_interfaces.setup)
            player.ifButton(friendchat_components.setup_enter, op = IfButtonOp.Op2)
            advance()
            assertEquals(FriendChatSettings.RANK_FRIEND, player.friendChat.enterRank)
            player.ifButton(friendchat_components.setup_kick, op = IfButtonOp.Op5)
            advance()
            assertEquals(3, player.friendChat.kickRank)

            val bob = registerPlayer()
            bob.displayName = "Bob"
            eventBus.publish(FriendChatJoinLeave(bob, "Alice"))
            assertMessageSent("You do not have a high enough rank to join this chat-channel.", bob)

            player.friendList += "Bob"
            eventBus.publish(FriendChatJoinLeave(bob, "Alice"))
            assertMessageSent("Now talking in chat-channel Alices Place", bob)

            player.ifButton(friendchat_components.setup_prefix, op = IfButtonOp.Op2)
            advance()
            assertNull(player.friendChat.name)
            assertMessageSent("The channel you were in has been disabled.", bob)
        }

    @Test
    fun GameTestState.`ranked friends may talk and kick`() =
        runGameTest(FriendChatScript::class) {
            player.displayName = "Alice"
            player.friendChat = player.friendChat.copy(name = "Alice", talkRank = 1, kickRank = 2)
            player.friendList += "Bob"
            val bob = registerPlayer()
            bob.displayName = "Bob"
            val carol = registerPlayer()
            carol.displayName = "Carol"
            eventBus.publish(FriendChatJoinLeave(bob, "Alice"))
            eventBus.publish(FriendChatJoinLeave(carol, "Alice"))

            eventBus.publish(FriendChatMessage(bob, "hi"))
            assertMessageSent(
                "You do not have a high enough rank to talk in this chat-channel.",
                bob,
            )

            eventBus.publish(FriendChatSetRank(player, "bob", 2))
            assertEquals(2, player.friendChatRanks["bob"])
            val bobClient = bob.client as CaptureClient
            bobClient.clearOutgoing()
            eventBus.publish(FriendChatMessage(bob, "hi"))
            assertEquals("hi", bobClient.filterIsInstance<MessageFriendChannel>().single().message)

            eventBus.publish(FriendChatKick(bob, "Carol"))
            assertMessageSent("You have been kicked from the channel.", carol)
            eventBus.publish(FriendChatJoinLeave(carol, "Alice"))
            assertMessageSent("You are temporarily banned from this chat-channel.", carol)
        }
}
