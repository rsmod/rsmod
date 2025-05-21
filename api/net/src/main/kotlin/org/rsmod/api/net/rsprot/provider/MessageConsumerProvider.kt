package org.rsmod.api.net.rsprot.provider

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.jvm.java
import net.rsprot.protocol.game.incoming.buttons.If3Button
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import net.rsprot.protocol.game.incoming.buttons.IfButtonT
import net.rsprot.protocol.game.incoming.locs.OpLoc
import net.rsprot.protocol.game.incoming.locs.OpLoc6
import net.rsprot.protocol.game.incoming.locs.OpLocT
import net.rsprot.protocol.game.incoming.messaging.MessagePublic
import net.rsprot.protocol.game.incoming.misc.client.MapBuildComplete
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
import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePNameDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePObjDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePStringDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import net.rsprot.protocol.message.codec.incoming.GameMessageConsumerRepositoryBuilder
import net.rsprot.protocol.message.codec.incoming.provider.DefaultGameMessageConsumerRepositoryProvider
import org.rsmod.api.net.rsprot.handlers.ClientCheatHandler
import org.rsmod.api.net.rsprot.handlers.CloseModalHandler
import org.rsmod.api.net.rsprot.handlers.If3ButtonHandler
import org.rsmod.api.net.rsprot.handlers.IfButtonDHandler
import org.rsmod.api.net.rsprot.handlers.IfButtonTHandler
import org.rsmod.api.net.rsprot.handlers.MapBuildCompleteHandler
import org.rsmod.api.net.rsprot.handlers.MessagePublicHandler
import org.rsmod.api.net.rsprot.handlers.MoveGameClickHandler
import org.rsmod.api.net.rsprot.handlers.MoveMinimapClickHandler
import org.rsmod.api.net.rsprot.handlers.OpLoc6Handler
import org.rsmod.api.net.rsprot.handlers.OpLocHandler
import org.rsmod.api.net.rsprot.handlers.OpLocTHandler
import org.rsmod.api.net.rsprot.handlers.OpNpc6Handler
import org.rsmod.api.net.rsprot.handlers.OpNpcHandler
import org.rsmod.api.net.rsprot.handlers.OpNpcTHandler
import org.rsmod.api.net.rsprot.handlers.OpObj6Handler
import org.rsmod.api.net.rsprot.handlers.OpObjHandler
import org.rsmod.api.net.rsprot.handlers.OpPlayerHandler
import org.rsmod.api.net.rsprot.handlers.ResumePCountDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePNameDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePObjDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePStringDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePauseButtonHandler
import org.rsmod.game.entity.Player

@Singleton
class MessageConsumerProvider
@Inject
constructor(
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
    private val messagePublic: MessagePublicHandler,
    private val if3Button: If3ButtonHandler,
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
) {
    fun get(): DefaultGameMessageConsumerRepositoryProvider<Player> {
        val builder = GameMessageConsumerRepositoryBuilder<Player>()
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
        builder.addListener(MessagePublic::class.java, messagePublic)
        builder.addListener(If3Button::class.java, if3Button)
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
