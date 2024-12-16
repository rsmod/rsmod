package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.misc.player.MessageGame
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.UnpackedObjType

/** Calls [mes] with [text] as the message and [ChatType.Spam] as the type of chat. */
public fun Player.spam(text: String): Unit = mes(text, ChatType.Spam)

/** @see [MessageGame] */
public fun Player.mes(text: String, type: ChatType = ChatType.GameMessage) {
    val message = MessageGame(type.id, text)
    client.write(message)
}

public fun Player.objExamine(type: UnpackedObjType, count: Int) {
    if (count >= 100_000) {
        mes("${count.formatAmount} x ${type.name}.")
    } else {
        mes(type.desc, ChatType.ObjExamine)
    }
}

public object GameMessage {
    /** @see [MessageGame] */
    public fun requestMes(
        player: Player,
        text: String,
        name: String,
        type: ChatType = ChatType.ChalReqTrade,
    ) {
        val message = MessageGame(type.id, name, text)
        player.client.write(message)
    }
}

public enum class ChatType(public val id: Int) {
    GameMessage(0),
    ModChat(1),
    PublicChat(2),
    PrivateChat(3),
    Engine(4),
    LoginLogoutNotification(5),
    PrivateChatOut(6),
    ModPrivateChat(7),
    FriendsChat(9),
    FriendsChatNotification(11),
    Broadcast(14),
    SnapshotFeedback(26),
    ObjExamine(27),
    NpcExamine(28),
    LocExamine(29),
    FriendNotification(30),
    IgnoreNotification(31),
    ClanChat(41),
    ClanMessage(43),
    ClanGuestChat(44),
    ClanGuestMessage(46),
    AutoTyper(90),
    ModAutoTyper(91),
    Console(99),
    TradeReq(101),
    Trade(102),
    ChalReqTrade(103),
    ChalReqFriendsChat(104),
    Spam(105),
    PlayerRelated(106),
    TenSecTimeout(107),
    Welcome(108),
    ClanCreationInvitation(109),
    ClanWarsChallenge(110),
    GimFormGroup(111),
    GimGroupWith(112),
    Dialogue(114),
    Mesbox(115),
}
