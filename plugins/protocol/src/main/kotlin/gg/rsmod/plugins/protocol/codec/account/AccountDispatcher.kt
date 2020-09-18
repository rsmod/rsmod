package gg.rsmod.plugins.protocol.codec.account

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import gg.rsmod.game.config.InternalConfig
import gg.rsmod.game.config.RsaConfig
import gg.rsmod.game.coroutine.IoCoroutineScope
import gg.rsmod.game.dispatch.GameJobDispatcher
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.domain.serializer.ClientDeserializeRequest
import gg.rsmod.game.model.domain.serializer.ClientDeserializeResponse
import gg.rsmod.game.model.domain.serializer.ClientSerializer
import gg.rsmod.game.model.mob.PlayerList
import gg.rsmod.plugins.protocol.codec.ResponseType
import gg.rsmod.plugins.protocol.codec.game.ChannelMessageListener
import gg.rsmod.plugins.protocol.codec.login.LoginDispatcher
import gg.rsmod.plugins.protocol.codec.login.LoginRequest
import gg.rsmod.plugins.protocol.codec.writeErrResponse
import gg.rsmod.util.IsaacRandom
import java.util.concurrent.ConcurrentLinkedQueue
import kotlinx.coroutines.launch

private val logger = InlineLogger()

class AccountDispatcher @Inject constructor(
    private val rsaConfig: RsaConfig,
    private val internalConfig: InternalConfig,
    private val ioCoroutineScope: IoCoroutineScope,
    private val gameJobDispatcher: GameJobDispatcher,
    private val loginDispatcher: LoginDispatcher,
    private val serializer: ClientSerializer,
    private val playerList: PlayerList
) {

    private val registerQueue = ConcurrentLinkedQueue<Account>()

    private val unregisterQueue = ConcurrentLinkedQueue<Client>()

    fun start() {
        gameJobDispatcher.schedule(::gameCycle)
        logger.debug { "Ready to dispatch incoming login requests" }
    }

    fun queue(request: LoginRequest) {
        ioCoroutineScope.launch {
            val account = request(request) ?: return@launch
            registerQueue.add(account)
        }
    }

    fun unregister(client: Client) {
        unregisterQueue.add(client)
    }

    private fun gameCycle() {
        for (i in 0 until internalConfig.logoutsPerCycle) {
            val logout = unregisterQueue.poll() ?: break
            playerList.remove(logout.player)
        }

        for (i in 0 until internalConfig.loginsPerCycle) {
            val account = registerQueue.poll() ?: break
            loginDispatcher.login(account)
        }
    }

    private fun request(request: LoginRequest): Account? {
        val channel = request.channel
        val xteas = request.xteas

        val clientRequest = ClientDeserializeRequest(
            loginName = request.username,
            plaintTextPass = request.password,
            loginXteas = request.xteas,
            settings = request.settings,
            machine = request.machine,
            messageListener = ChannelMessageListener(channel)
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
}
