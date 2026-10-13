package org.runerealm.content.other.social

import jakarta.inject.Inject
import net.rsprot.protocol.game.incoming.social.FriendListAdd
import net.rsprot.protocol.game.incoming.social.FriendListDel
import net.rsprot.protocol.game.incoming.social.IgnoreListAdd
import net.rsprot.protocol.game.incoming.social.IgnoreListDel
import net.rsprot.protocol.game.outgoing.social.FriendListLoaded
import net.rsprot.protocol.game.outgoing.social.UpdateFriendList
import net.rsprot.protocol.game.outgoing.social.UpdateIgnoreList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.interfaces
import org.runerealm.api.net.rsprot.handlers.FriendListAddHandler
import org.runerealm.api.net.rsprot.handlers.FriendListDelHandler
import org.runerealm.api.net.rsprot.handlers.IgnoreListAddHandler
import org.runerealm.api.net.rsprot.handlers.IgnoreListDelHandler
import org.runerealm.api.player.events.social.SocialEvents
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.capture.CaptureClient
import org.runerealm.api.testing.scope.GameTestScope
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.SessionStateEvent

class SocialScriptTest {
    @Test
    fun GameTestState.`empty lists are still sent on login so the tabs stop loading`() =
        runGameTest(SocialScript::class) {
            val client = player.client as CaptureClient
            client.clearOutgoing()

            eventBus.publish(SessionStateEvent.Login(player))

            val friends = client.filterIsInstance<UpdateFriendList>().single()
            assertTrue(friends.friends.isEmpty())
            val ignores = client.filterIsInstance<UpdateIgnoreList>().single()
            assertTrue(ignores.ignores.isEmpty())
            // FRIENDLIST_LOADED only means "connecting" to the client: it must not follow the list.
            assertTrue(client.filterIsInstance<FriendListLoaded>().isEmpty())
        }

    @Test
    fun GameTestState.`saved lists are sent on login`() =
        runGameTest(SocialScript::class) {
            player.displayName = "Alice"
            player.friendList += "Bob"
            player.friendList += "Carol"
            player.ignoreList += "Dave"
            val bob = registerPlayer()
            bob.displayName = "Bob"
            val client = player.client as CaptureClient
            client.clearOutgoing()

            eventBus.publish(SessionStateEvent.Login(player))

            val friends = client.filterIsInstance<UpdateFriendList>().single().friends
            assertEquals(listOf("Bob", "Carol"), friends.map { it.name })
            assertTrue(friends[0] is UpdateFriendList.OnlineFriend)
            assertTrue(friends[1] is UpdateFriendList.OfflineFriend)
            val ignores = client.filterIsInstance<UpdateIgnoreList>().single().ignores
            assertEquals(
                listOf("Dave"),
                ignores.map { (it as UpdateIgnoreList.AddedIgnoredEntry).name },
            )
        }

    @Test
    fun GameTestState.`friend and ignore packets from the client update the lists`() =
        runSocialTest { deps ->
            player.displayName = "Alice"
            val bob = registerPlayer()
            bob.displayName = "Bob"
            val client = player.captureClient
            client.clearOutgoing()

            // The tab's "Add friend" button and right-click "Add friend" both send this packet.
            client.queue(deps.friendAdd, FriendListAdd("bob"))
            advance()
            assertEquals(listOf("Bob"), player.friendList)
            val added = client.filterIsInstance<UpdateFriendList>().single().friends.single()
            assertTrue(added is UpdateFriendList.OnlineFriend)
            assertTrue(added.added)

            client.queue(deps.friendDel, FriendListDel("Bob"))
            advance()
            assertTrue(player.friendList.isEmpty())

            client.clearOutgoing()
            client.queue(deps.ignoreAdd, IgnoreListAdd("Bob"))
            advance()
            assertEquals(listOf("Bob"), player.ignoreList)
            assertTrue(client.filterIsInstance<UpdateIgnoreList>().isNotEmpty())

            client.queue(deps.ignoreDel, IgnoreListDel("bob"))
            advance()
            assertTrue(player.ignoreList.isEmpty())
        }

    @Test
    fun GameTestState.`adding names follows the wiki rules`() =
        runGameTest(SocialScript::class) {
            player.displayName = "Alice"

            eventBus.publish(SocialEvents.FriendAdd(player, "alice"))
            assertMessageSent("You can't add yourself to your own friend list.")

            eventBus.publish(SocialEvents.FriendAdd(player, "Bob"))
            eventBus.publish(SocialEvents.FriendAdd(player, "bob"))
            assertMessageSent("bob is already on your friend list.")

            eventBus.publish(SocialEvents.IgnoreAdd(player, "Bob"))
            assertMessageSent("Please remove Bob from your friend list first.")
            assertTrue(player.ignoreList.isEmpty(), "friends cannot also be ignored")

            eventBus.publish(SocialEvents.IgnoreAdd(player, "Carol"))
            eventBus.publish(SocialEvents.FriendAdd(player, "Carol"))
            assertMessageSent("Please remove Carol from your ignore list first.")

            player.friendList.clear()
            repeat(400) { player.friendList += "Friend$it" }
            eventBus.publish(SocialEvents.FriendAdd(player, "Dave"))
            assertMessageSent(
                "Your friend list is full. Max of 200 for free users, and 400 for members."
            )
            assertEquals(400, player.friendList.size)
        }

    @Test
    fun GameTestState.`friend list holds 400 names without membership`() =
        runGameTest(SocialScript::class) {
            repeat(250) { player.friendList += "Friend$it" }
            eventBus.publish(SocialEvents.FriendAdd(player, "Dave"))
            assertEquals("Dave", player.friendList.last())
        }

    @Test
    fun GameTestState.`friends are told when you log in and out`() =
        runGameTest(SocialScript::class) {
            player.displayName = "Alice"
            player.friendList += "Bob"
            val bob = registerPlayer()
            bob.displayName = "Bob"
            val client = player.client as CaptureClient

            client.clearOutgoing()
            eventBus.publish(SessionStateEvent.Logout(bob))
            val offline = client.filterIsInstance<UpdateFriendList>().single().friends.single()
            assertTrue(offline is UpdateFriendList.OfflineFriend)

            client.clearOutgoing()
            eventBus.publish(SessionStateEvent.Login(bob))
            val online = client.filterIsInstance<UpdateFriendList>().single().friends.single()
            assertTrue(online is UpdateFriendList.OnlineFriend)
        }

    @Test
    fun GameTestState.`ignoring a friend makes you appear offline to them`() =
        runGameTest(SocialScript::class) {
            player.displayName = "Alice"
            val bob = registerPlayer()
            bob.displayName = "Bob"
            bob.friendList += "Alice"
            val bobClient = bob.client as CaptureClient

            bobClient.clearOutgoing()
            eventBus.publish(SocialEvents.IgnoreAdd(player, "Bob"))
            val offline = bobClient.filterIsInstance<UpdateFriendList>().single().friends.single()
            assertTrue(offline is UpdateFriendList.OfflineFriend)

            bobClient.clearOutgoing()
            eventBus.publish(SocialEvents.IgnoreDel(player, "Bob"))
            val online = bobClient.filterIsInstance<UpdateFriendList>().single().friends.single()
            assertTrue(online is UpdateFriendList.OnlineFriend)
        }

    @Test
    fun GameTestState.`view ignore list switches the tab and back`() =
        runGameTest(SocialScript::class) {
            player.ifOpenOverlay(interfaces.friends, components.toplevel_target_side9)
            player.ifButton(social_components.view_ignore)
            advance()
            assertTrue(player.ui.containsOverlay(social_interfaces.ignore))

            player.ifButton(social_components.view_friends)
            advance()
            assertTrue(player.ui.containsOverlay(interfaces.friends))
        }

    private val Player.captureClient: CaptureClient
        get() = client as CaptureClient

    private fun GameTestState.runSocialTest(body: GameTestScope.(SocialTestDependencies) -> Unit) =
        runInjectedGameTest(SocialTestDependencies::class, null, SocialScript::class) { deps ->
            body(deps)
        }
}

class SocialTestDependencies
@Inject
constructor(
    val friendAdd: FriendListAddHandler,
    val friendDel: FriendListDelHandler,
    val ignoreAdd: IgnoreListAddHandler,
    val ignoreDel: IgnoreListDelHandler,
)
