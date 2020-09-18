package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.action.ActionHandlerMap
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.Device
import gg.rsmod.plugins.protocol.codec.HandshakeConstants
import gg.rsmod.plugins.protocol.codec.ResponseType
import gg.rsmod.plugins.protocol.codec.account.Account
import gg.rsmod.plugins.protocol.codec.game.GameSessionDecoder
import gg.rsmod.plugins.protocol.codec.game.GameSessionEncoder
import gg.rsmod.plugins.protocol.codec.game.GameSessionHandler
import gg.rsmod.plugins.protocol.codec.writeErrResponse
import gg.rsmod.plugins.protocol.packet.server.PlayerInfo
import gg.rsmod.plugins.protocol.packet.server.RebuildNormal
import gg.rsmod.util.IsaacRandom
import io.netty.channel.Channel
import io.netty.channel.ChannelPipeline

private val logger = InlineLogger()

class LoginDispatcher @Inject constructor(
    private val playerList: PlayerList,
    private val eventBus: EventBus,
    private val xteas: XteaRepository,
    private val actionHandlers: ActionHandlerMap,
    private val desktopStructures: DesktopPacketStructure
) {

    fun login(account: Account) {
        val channel = account.channel
        val client = account.client
        val device = account.device
        val decodeIsaac = account.decodeIsaac
        val encodeIsaac = account.encodeIsaac

        val online = playerList.any { it?.id?.value == client.player.id.value }
        if (online) {
            channel.writeErrResponse(ResponseType.ACCOUNT_ONLINE)
            return
        }

        val registered = playerList.register(client.player)
        if (!registered) {
            channel.writeErrResponse(ResponseType.WORLD_FULL)
            return
        }
        client.register(channel, device, decodeIsaac, encodeIsaac)
    }

    private fun Client.register(
        channel: Channel,
        device: Device,
        decodeIsaac: IsaacRandom,
        encodeIsaac: IsaacRandom
    ) {
        val gpi = player.gpi()
        val reconnect = false
        writeResponse(channel, encodeIsaac, reconnect, gpi)
        channel.pipeline().applyGameCodec(
            this,
            device,
            decodeIsaac,
            encodeIsaac
        )
        player.login(reconnect, gpi)
    }

    private fun Client.writeResponse(
        channel: Channel,
        encodeIsaac: IsaacRandom,
        reconnect: Boolean,
        gpi: PlayerInfo
    ) {
        val response = if (reconnect) {
            LoginResponse.Reconnect(gpi)
        } else {
            LoginResponse.Normal(
                playerIndex = player.index,
                privilege = player.entity.privilege,
                moderator = true,
                rememberDevice = false,
                encodeIsaac = encodeIsaac,
                members = true
            )
        }
        channel.write(response)
        channel.flush()
    }

    private fun ChannelPipeline.applyGameCodec(
        client: Client,
        device: Device,
        decodeIsaac: IsaacRandom,
        encodeIsaac: IsaacRandom
    ) {
        val structures = when (device) {
            Device.Desktop -> desktopStructures
            else -> TODO()
        }
        val decoder = GameSessionDecoder(decodeIsaac, structures.client, actionHandlers)
        val encoder = GameSessionEncoder(encodeIsaac, structures.server)
        val handler = GameSessionHandler(client)

        remove(HandshakeConstants.RESPONSE_PIPELINE)

        replace(
            HandshakeConstants.DECODER_PIPELINE,
            HandshakeConstants.DECODER_PIPELINE,
            decoder
        )
        replace(
            HandshakeConstants.ENCODER_PIPELINE,
            HandshakeConstants.ENCODER_PIPELINE,
            encoder
        )
        replace(
            HandshakeConstants.ADAPTER_PIPELINE,
            HandshakeConstants.ADAPTER_PIPELINE,
            handler
        )
    }

    private fun Player.login(reconnect: Boolean, gpi: PlayerInfo) {
        if (!reconnect) {
            val rebuildNormal = RebuildNormal(
                gpi = gpi,
                zoneX = coords.x shr 3,
                zoneY = coords.y shr 3,
                xteas = xteas
            )
            write(rebuildNormal)
            flush()
        }
        login(eventBus)
    }

    private fun PlayerList.playerCoords(excludeIndex: Int): IntArray {
        var index = 0
        val coordinates = IntArray(capacity - 1)
        for (i in indices) {
            if (i == excludeIndex) {
                continue
            }
            val player = this[i]
            val coords = player?.coords?.packed18Bits ?: 0
            coordinates[index++] = coords
        }
        return coordinates
    }

    private fun Player.gpi() = PlayerInfo(
        playerCoordsAs30Bits = coords.packed30Bits,
        otherPlayerCoords = playerList.playerCoords(index)
    )
}
