package org.rsmod.plugins.api.protocol.codec.account

import com.github.michaelbull.logging.InlineLogger
import com.github.michaelbull.retry.RetryFailure
import com.github.michaelbull.retry.RetryInstruction
import com.github.michaelbull.retry.policy.binaryExponentialBackoff
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.retry
import com.google.inject.Inject
import io.netty.channel.Channel
import io.netty.channel.ChannelPipeline
import java.util.concurrent.ConcurrentLinkedQueue
import kotlinx.coroutines.launch
import org.rsmod.game.action.ActionBus
import org.rsmod.game.config.InternalConfig
import org.rsmod.game.config.RsaConfig
import org.rsmod.game.coroutine.IoCoroutineScope
import org.rsmod.game.dispatch.GameJobDispatcher
import org.rsmod.game.event.EventBus
import org.rsmod.game.event.impl.ClientRegister
import org.rsmod.game.event.impl.ClientUnregister
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.client.ClientList
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.domain.serializer.ClientDeserializeRequest
import org.rsmod.game.model.domain.serializer.ClientDeserializeResponse
import org.rsmod.game.model.domain.serializer.ClientSerializer
import org.rsmod.game.model.map.MapIsolation
import org.rsmod.game.model.map.Viewport
import org.rsmod.game.model.map.viewport
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.plugins.api.model.map.of
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.codec.HandshakeConstants
import org.rsmod.plugins.api.protocol.codec.ResponseType
import org.rsmod.plugins.api.protocol.codec.game.ChannelMessageListener
import org.rsmod.plugins.api.protocol.codec.game.GameSessionDecoder
import org.rsmod.plugins.api.protocol.codec.game.GameSessionEncoder
import org.rsmod.plugins.api.protocol.codec.game.GameSessionHandler
import org.rsmod.plugins.api.protocol.codec.login.LoginRequest
import org.rsmod.plugins.api.protocol.codec.login.LoginResponse
import org.rsmod.plugins.api.protocol.codec.writeErrResponse
import org.rsmod.plugins.api.protocol.packet.server.InitialPlayerInfo
import org.rsmod.plugins.api.protocol.packet.server.RebuildNormal
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import org.rsmod.util.security.IsaacRandom

private val logger = InlineLogger()

private const val SERIALIZE_ATTEMPTS = 5
private const val BACKOFF_BASE = 100L
private const val BACKOFF_MAX = 10000L

class AccountDispatcher @Inject constructor(
    private val rsaConfig: RsaConfig,
    private val internalConfig: InternalConfig,
    private val ioCoroutineScope: IoCoroutineScope,
    private val gameJobDispatcher: GameJobDispatcher,
    private val serializer: ClientSerializer,
    private val playerList: PlayerList,
    private val clientList: ClientList,
    private val deviceStructures: DevicePacketStructureMap,
    private val xteas: XteaRepository,
    private val eventBus: EventBus,
    private val actionBus: ActionBus,
    private val mapIsolation: MapIsolation
) {

    private val registerQueue = ConcurrentLinkedQueue<Account>()

    private val unregisterQueue = ConcurrentLinkedQueue<Client>()

    fun start() {
        gameJobDispatcher.schedule(::gameCycle)
        logger.debug { "Ready to dispatch incoming login requests" }
    }

    fun register(request: LoginRequest) {
        ioCoroutineScope.launch {
            val account = deserialize(request) ?: return@launch
            registerQueue.add(account)
        }
    }

    fun unregister(client: Client) {
        ioCoroutineScope.launch {
            retry(serializePolicy()) {
                serializer.serialize(client)
                unregisterQueue.add(client)
            }
        }
    }

    private fun gameCycle() {
        for (i in 0 until internalConfig.loginsPerCycle) {
            val account = registerQueue.poll() ?: break
            logger.debug { "Register account to game (account=$account)" }
            login(account)
        }

        for (i in 0 until internalConfig.logoutsPerCycle) {
            val client = unregisterQueue.poll() ?: break
            logger.debug { "Unregister player from game (player=${client.player})" }
            logout(client)
        }
    }

    private fun deserialize(request: LoginRequest): Account? {
        val channel = request.channel
        val xteas = request.xteas

        val clientRequest = ClientDeserializeRequest(
            loginName = request.username,
            device = request.device,
            plaintTextPass = request.password,
            loginXteas = request.xteas,
            reconnectXteas = request.reconnectXteas,
            settings = request.settings,
            machine = request.machine,
            eventBus = eventBus,
            actionBus = actionBus,
            messageListener = ChannelMessageListener(channel),
            bufAllocator = channel.alloc()
        )
        val deserialize = serializer.deserialize(clientRequest)
        logger.debug { "Deserialized login request (request=$request, response=$deserialize)" }
        when (deserialize) {
            is ClientDeserializeResponse.BadCredentials -> {
                channel.writeErrResponse(ResponseType.INVALID_CREDENTIALS)
                return null
            }
            is ClientDeserializeResponse.ReadError -> {
                channel.writeErrResponse(ResponseType.COULD_NOT_COMPLETE_LOGIN)
                return null
            }
            is ClientDeserializeResponse.Success -> {
                val client = deserialize.client
                val decodeIsaac = if (rsaConfig.isEnabled) IsaacRandom() else IsaacRandom.ZERO
                val encodeIsaac = if (rsaConfig.isEnabled) IsaacRandom() else IsaacRandom.ZERO
                if (rsaConfig.isEnabled) {
                    decodeIsaac.init(xteas)
                    encodeIsaac.init(IntArray(xteas.size) { xteas[it] + 50 })
                }
                return Account(
                    channel = channel,
                    client = client,
                    device = request.device,
                    decodeIsaac = decodeIsaac,
                    encodeIsaac = encodeIsaac
                )
            }
        }
    }

    private fun login(account: Account) {
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
        clientList.register(client)
        eventBus.publish(ClientRegister(client))
        client.register(channel, device, decodeIsaac, encodeIsaac)
    }

    private fun logout(client: Client) {
        clientList.remove(client)
        playerList.remove(client.player)
        eventBus.publish(ClientUnregister(client))
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
        channel.flush()
    }

    private fun Client.writeResponse(
        channel: Channel,
        encodeIsaac: IsaacRandom,
        reconnect: Boolean,
        gpi: InitialPlayerInfo
    ) {
        val response = if (reconnect) {
            LoginResponse.Reconnect(gpi)
        } else {
            LoginResponse.Normal(
                playerIndex = player.index,
                rank = player.entity.rank,
                moderator = true,
                rememberDevice = false,
                encodeIsaac = encodeIsaac,
                members = true
            )
        }
        channel.writeAndFlush(response)
    }

    private fun ChannelPipeline.applyGameCodec(
        client: Client,
        device: Device,
        decodeIsaac: IsaacRandom,
        encodeIsaac: IsaacRandom
    ) {
        val structures = device.packetStructures()
        val decoder = GameSessionDecoder(decodeIsaac, structures.client)
        val encoder = GameSessionEncoder(encodeIsaac, structures.server)
        val handler = GameSessionHandler(client, this@AccountDispatcher)

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

    private fun Player.login(reconnect: Boolean, gpi: InitialPlayerInfo) {
        val newViewport = coords.zone().viewport(mapIsolation)
        if (!reconnect) {
            val rebuildNormal = RebuildNormal(
                gpi = gpi,
                playerZone = coords.zone(),
                viewport = newViewport,
                xteas = xteas
            )
            write(rebuildNormal)
            flush()
        }
        viewport = Viewport.of(coords, newViewport)
        login()
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

    private fun Player.gpi() = InitialPlayerInfo(
        playerCoordsAs30Bits = coords.packed30Bits,
        otherPlayerCoords = playerList.playerCoords(index)
    )

    private fun Device.packetStructures() = deviceStructures.getCodec(this)

    private fun serializePolicy(): suspend RetryFailure<Throwable>.() -> RetryInstruction {
        return limitAttempts(SERIALIZE_ATTEMPTS) + binaryExponentialBackoff(BACKOFF_BASE, BACKOFF_MAX)
    }
}
