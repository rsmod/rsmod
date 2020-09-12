package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.action.ActionHandlerMap
import gg.rsmod.game.config.RsaConfig
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.game.model.mob.Player
import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.Device
import gg.rsmod.plugins.protocol.codec.HandshakeConstants
import gg.rsmod.plugins.protocol.codec.game.ChannelMessageListener
import gg.rsmod.plugins.protocol.codec.game.GameSessionDecoder
import gg.rsmod.plugins.protocol.codec.game.GameSessionEncoder
import gg.rsmod.plugins.protocol.codec.game.GameSessionHandler
import gg.rsmod.plugins.protocol.packet.server.PlayerInfo
import gg.rsmod.plugins.protocol.packet.server.RebuildNormal
import gg.rsmod.util.IsaacRandom

private val logger = InlineLogger()

class LoginDispatcher @Inject constructor(
    private val rsaConfig: RsaConfig,
    private val eventBus: EventBus,
    private val xteas: XteaRepository,
    private val actionHandlers: ActionHandlerMap,
    private val desktopStructures: DesktopPacketStructure
) {

    fun add(request: LoginRequest) {
        logger.info { "Add login request: $request" }
        registerGameSession(request)
    }

    private fun registerGameSession(request: LoginRequest) {
        val channel = request.channel
        val machine = request.machine
        val settings = request.settings
        val xtea = request.xtea
        val username = request.username

        val decodeIsaac = if (!rsaConfig.isEnabled) IsaacRandom.ZERO else IsaacRandom()
        val encodeIsaac = if (!rsaConfig.isEnabled) IsaacRandom.ZERO else IsaacRandom()

        val player = Player(
            loginName = username,
            entity = PlayerEntity(
                username = request.username,
                privilege = 0
            ),
            messageListeners = listOf(
                ChannelMessageListener(channel)
            )
        )
        val client = Client(
            player = player,
            machine = machine,
            settings = settings
        )

        val device = Device.Desktop
        val structures = when (device) {
            Device.Desktop -> desktopStructures
            else -> TODO()
        }

        if (rsaConfig.isEnabled) {
            decodeIsaac.init(xtea)
            encodeIsaac.init(IntArray(xtea.size) { xtea[it] + 50 })
        }

        val decoder = GameSessionDecoder(decodeIsaac, structures.client, actionHandlers)
        val encoder = GameSessionEncoder(encodeIsaac, structures.server)
        val handler = GameSessionHandler(client)

        val gpi = PlayerInfo(
            playerCoordsAs30Bits = player.coords.packed30Bits,
            otherPlayerCoords = IntArray(2046)
        )

        val response: LoginResponse = LoginNormalResponse(
            playerIndex = 1,
            privilege = 0,
            moderator = true,
            rememberDevice = true,
            encodeIsaac = encodeIsaac,
            members = true
        )
        channel.write(response)
        channel.flush()

        channel.pipeline().remove(
            HandshakeConstants.RESPONSE_PIPELINE
        )

        channel.pipeline().replace(
            HandshakeConstants.DECODER_PIPELINE,
            HandshakeConstants.DECODER_PIPELINE,
            decoder
        )

        channel.pipeline().replace(
            HandshakeConstants.ENCODER_PIPELINE,
            HandshakeConstants.ENCODER_PIPELINE,
            encoder
        )

        channel.pipeline().replace(
            HandshakeConstants.ADAPTER_PIPELINE,
            HandshakeConstants.ADAPTER_PIPELINE,
            handler
        )

        when (response) {
            is LoginNormalResponse -> {
                val rebuildNormal = RebuildNormal(
                    gpi = gpi,
                    zoneX = player.coords.x shr 3,
                    zoneY = player.coords.y shr 3,
                    xteas = xteas
                )
                player.write(rebuildNormal)
                player.flush()
                player.login(eventBus)
            }
            is LoginReconnectResponse -> player.login(eventBus)
            else -> logger.error { "Unhandled login connection type (type=$response)" }
        }
    }
}
