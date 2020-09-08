package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.model.Client
import gg.rsmod.game.model.domain.repo.XteaRepository
import gg.rsmod.plugins.protocol.DesktopPacketStructure
import gg.rsmod.plugins.protocol.Device
import gg.rsmod.plugins.protocol.codec.HandshakeConstants
import gg.rsmod.plugins.protocol.codec.game.GameSessionEncoder
import gg.rsmod.plugins.protocol.codec.game.GameSessionHandler
import gg.rsmod.plugins.protocol.codec.game.ChannelMessageListener
import gg.rsmod.plugins.protocol.codec.game.GameSessionDecoder
import gg.rsmod.plugins.protocol.packet.server.InitializeGpi
import gg.rsmod.plugins.protocol.packet.server.RebuildNormal
import gg.rsmod.util.IsaacRandom

private val logger = InlineLogger()

class LoginDispatcher @Inject constructor(
    private val desktopStructures: DesktopPacketStructure,
    private val xteas: XteaRepository
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

        val response = LoginResponse(
            privilege = 0,
            playerIndex = 1
        )
        channel.write(response)
        channel.flush()

        val player = Player(
            loginName = request.username,
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

        val decodeIsaac = IsaacRandom()
        decodeIsaac.init(xtea)

        val encodeIsaac = IsaacRandom()
        encodeIsaac.init(IntArray(xtea.size) { xtea[it] + 50 })

        val decoder = GameSessionDecoder(decodeIsaac, structures.client)
        val encoder = GameSessionEncoder(encodeIsaac, structures.server)
        val handler = GameSessionHandler(client)

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

        val gpi = InitializeGpi(
            playerCoordsAs30Bits = player.coords.packed30Bits,
            otherPlayerCoords = IntArray(2046)
        )

        val rebuildNormal = RebuildNormal(
            gpi = gpi,
            zoneX = player.coords.x shr 3,
            zoneY = player.coords.y shr 3,
            xteas = xteas
        )

        player.write(rebuildNormal)
        player.flush()
    }
}
