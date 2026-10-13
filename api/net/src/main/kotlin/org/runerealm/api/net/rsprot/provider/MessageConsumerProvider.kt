package org.runerealm.api.net.rsprot.provider

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.jvm.java
import net.rsprot.protocol.game.incoming.buttons.If3Button
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import net.rsprot.protocol.game.incoming.buttons.IfButtonT
import net.rsprot.protocol.game.incoming.buttons.IfSubOp
import net.rsprot.protocol.game.incoming.friendchat.FriendChatJoinLeave
import net.rsprot.protocol.game.incoming.friendchat.FriendChatKick
import net.rsprot.protocol.game.incoming.friendchat.FriendChatSetRank
import net.rsprot.protocol.game.incoming.locs.OpLoc
import net.rsprot.protocol.game.incoming.locs.OpLoc6
import net.rsprot.protocol.game.incoming.locs.OpLocT
import net.rsprot.protocol.game.incoming.messaging.MessagePublic
import net.rsprot.protocol.game.incoming.misc.client.MapBuildComplete
import net.rsprot.protocol.game.incoming.misc.client.WindowStatus
import net.rsprot.protocol.game.incoming.misc.user.ClientCheat
import net.rsprot.protocol.game.incoming.misc.user.CloseModal
import net.rsprot.protocol.game.incoming.misc.user.MoveGameClick
import net.rsprot.protocol.game.incoming.misc.user.MoveMinimapClick
import net.rsprot.protocol.game.incoming.npcs.OpNpc
import net.rsprot.protocol.game.incoming.npcs.OpNpc6
import net.rsprot.protocol.game.incoming.npcs.OpNpcT
import net.rsprot.protocol.game.incoming.objs.OpObj
import net.rsprot.protocol.game.incoming.objs.OpObj6
import net.rsprot.protocol.game.incoming.players.OpPlayer
import net.rsprot.protocol.game.incoming.players.OpPlayerT
import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePNameDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePObjDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePStringDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import net.rsprot.protocol.game.incoming.social.FriendListAdd
import net.rsprot.protocol.game.incoming.social.FriendListDel
import net.rsprot.protocol.game.incoming.social.IgnoreListAdd
import net.rsprot.protocol.game.incoming.social.IgnoreListDel
import net.rsprot.protocol.message.codec.incoming.GameMessageConsumerRepositoryBuilder
import net.rsprot.protocol.message.codec.incoming.provider.DefaultGameMessageConsumerRepositoryProvider
import org.runerealm.api.net.rsprot.handlers.ClientCheatHandler
import org.runerealm.api.net.rsprot.handlers.CloseModalHandler
import org.runerealm.api.net.rsprot.handlers.FriendChatJoinLeaveHandler
import org.runerealm.api.net.rsprot.handlers.FriendChatKickHandler
import org.runerealm.api.net.rsprot.handlers.FriendChatSetRankHandler
import org.runerealm.api.net.rsprot.handlers.FriendListAddHandler
import org.runerealm.api.net.rsprot.handlers.FriendListDelHandler
import org.runerealm.api.net.rsprot.handlers.If3ButtonHandler
import org.runerealm.api.net.rsprot.handlers.IfButtonDHandler
import org.runerealm.api.net.rsprot.handlers.IfButtonTHandler
import org.runerealm.api.net.rsprot.handlers.IfSubOpHandler
import org.runerealm.api.net.rsprot.handlers.IgnoreListAddHandler
import org.runerealm.api.net.rsprot.handlers.IgnoreListDelHandler
import org.runerealm.api.net.rsprot.handlers.MapBuildCompleteHandler
import org.runerealm.api.net.rsprot.handlers.MessagePublicHandler
import org.runerealm.api.net.rsprot.handlers.MoveGameClickHandler
import org.runerealm.api.net.rsprot.handlers.MoveMinimapClickHandler
import org.runerealm.api.net.rsprot.handlers.OpLoc6Handler
import org.runerealm.api.net.rsprot.handlers.OpLocHandler
import org.runerealm.api.net.rsprot.handlers.OpLocTHandler
import org.runerealm.api.net.rsprot.handlers.OpNpc6Handler
import org.runerealm.api.net.rsprot.handlers.OpNpcHandler
import org.runerealm.api.net.rsprot.handlers.OpNpcTHandler
import org.runerealm.api.net.rsprot.handlers.OpObj6Handler
import org.runerealm.api.net.rsprot.handlers.OpObjHandler
import org.runerealm.api.net.rsprot.handlers.OpPlayerHandler
import org.runerealm.api.net.rsprot.handlers.OpPlayerTHandler
import org.runerealm.api.net.rsprot.handlers.ResumePCountDialogHandler
import org.runerealm.api.net.rsprot.handlers.ResumePNameDialogHandler
import org.runerealm.api.net.rsprot.handlers.ResumePObjDialogHandler
import org.runerealm.api.net.rsprot.handlers.ResumePStringDialogHandler
import org.runerealm.api.net.rsprot.handlers.ResumePauseButtonHandler
import org.runerealm.api.net.rsprot.handlers.WindowStatusHandler
import org.runerealm.game.entity.Player

@Singleton
class MessageConsumerProvider
@Inject
constructor(
    private val windowStatus: WindowStatusHandler,
    private val moveGameClick: MoveGameClickHandler,
    private val moveMinimapClick: MoveMinimapClickHandler,
    private val opLoc: OpLocHandler,
    private val opLocT: OpLocTHandler,
    private val opLoc6: OpLoc6Handler,
    private val clientCheat: ClientCheatHandler,
    private val opNpc: OpNpcHandler,
    private val opNpcT: OpNpcTHandler,
    private val opNpc6: OpNpc6Handler,
    private val opPlayer: OpPlayerHandler,
    private val opPlayerT: OpPlayerTHandler,
    private val messagePublic: MessagePublicHandler,
    private val if3Button: If3ButtonHandler,
    private val ifSubOp: IfSubOpHandler,
    private val closeModal: CloseModalHandler,
    private val resumePauseButton: ResumePauseButtonHandler,
    private val opObj: OpObjHandler,
    private val opObj6: OpObj6Handler,
    private val resumePCountDialog: ResumePCountDialogHandler,
    private val resumePNameDialog: ResumePNameDialogHandler,
    private val resumePStringDialog: ResumePStringDialogHandler,
    private val resumePObjDialog: ResumePObjDialogHandler,
    private val ifButtonD: IfButtonDHandler,
    private val ifButtonT: IfButtonTHandler,
    private val mapBuildComplete: MapBuildCompleteHandler,
    private val friendChatJoinLeave: FriendChatJoinLeaveHandler,
    private val ignoreListDel: IgnoreListDelHandler,
    private val ignoreListAdd: IgnoreListAddHandler,
    private val friendListDel: FriendListDelHandler,
    private val friendListAdd: FriendListAddHandler,
    private val friendChatKick: FriendChatKickHandler,
    private val friendChatSetRank: FriendChatSetRankHandler,
) {
    fun get(): DefaultGameMessageConsumerRepositoryProvider<Player> {
        val builder = GameMessageConsumerRepositoryBuilder<Player>()
        builder.addListener(WindowStatus::class.java, windowStatus)
        builder.addListener(FriendChatJoinLeave::class.java, friendChatJoinLeave)
        builder.addListener(FriendChatKick::class.java, friendChatKick)
        builder.addListener(FriendChatSetRank::class.java, friendChatSetRank)
        builder.addListener(IgnoreListDel::class.java, ignoreListDel)
        builder.addListener(IgnoreListAdd::class.java, ignoreListAdd)
        builder.addListener(FriendListDel::class.java, friendListDel)
        builder.addListener(FriendListAdd::class.java, friendListAdd)
        builder.addListener(MoveGameClick::class.java, moveGameClick)
        builder.addListener(MoveMinimapClick::class.java, moveMinimapClick)
        builder.addListener(OpLoc::class.java, opLoc)
        builder.addListener(OpLocT::class.java, opLocT)
        builder.addListener(OpLoc6::class.java, opLoc6)
        builder.addListener(ClientCheat::class.java, clientCheat)
        builder.addListener(OpNpc::class.java, opNpc)
        builder.addListener(OpNpcT::class.java, opNpcT)
        builder.addListener(OpNpc6::class.java, opNpc6)
        builder.addListener(OpPlayer::class.java, opPlayer)
        builder.addListener(OpPlayerT::class.java, opPlayerT)
        builder.addListener(MessagePublic::class.java, messagePublic)
        builder.addListener(If3Button::class.java, if3Button)
        builder.addListener(IfSubOp::class.java, ifSubOp)
        builder.addListener(CloseModal::class.java, closeModal)
        builder.addListener(ResumePauseButton::class.java, resumePauseButton)
        builder.addListener(OpObj::class.java, opObj)
        builder.addListener(OpObj6::class.java, opObj6)
        builder.addListener(ResumePCountDialog::class.java, resumePCountDialog)
        builder.addListener(ResumePNameDialog::class.java, resumePNameDialog)
        builder.addListener(ResumePStringDialog::class.java, resumePStringDialog)
        builder.addListener(ResumePObjDialog::class.java, resumePObjDialog)
        builder.addListener(IfButtonD::class.java, ifButtonD)
        builder.addListener(IfButtonT::class.java, ifButtonT)
        builder.addListener(MapBuildComplete::class.java, mapBuildComplete)
        return DefaultGameMessageConsumerRepositoryProvider(builder.build())
    }
}
