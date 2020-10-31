package gg.rsmod.plugins.content.serializer

import com.google.inject.Inject
import gg.rsmod.game.config.GameConfig
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.PlayerId
import gg.rsmod.game.model.domain.serializer.ClientData
import gg.rsmod.game.model.domain.serializer.ClientDataMapper
import gg.rsmod.game.model.domain.serializer.ClientDeserializeRequest
import gg.rsmod.game.model.domain.serializer.ClientDeserializeResponse
import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.model.mob.Player
import gg.rsmod.util.security.PasswordEncryption
import kotlin.reflect.KClass

class DefaultClientMapper @Inject constructor(
    private val config: GameConfig,
    private val encryption: PasswordEncryption
) : ClientDataMapper<DefaultClientData> {

    override val type = DefaultClientData::class

    override fun deserialize(request: ClientDeserializeRequest, data: DefaultClientData): ClientDeserializeResponse {
        val password = request.plaintTextPass
        if (password == null) {
            val reconnectXteas = request.reconnectXteas
            if (reconnectXteas == null || !reconnectXteas.contentEquals(data.loginXteas)) {
                return ClientDeserializeResponse.BadCredentials
            }
        } else if (!encryption.verify(password, data.encryptedPass)) {
            return ClientDeserializeResponse.BadCredentials
        }
        val entity = PlayerEntity(
            username = data.displayName,
            rank = data.rank
        )
        val player = Player(
            id = PlayerId(data.loginName),
            loginName = data.loginName,
            eventBus = request.eventBus,
            actionBus = request.actionBus,
            entity = entity,
            messageListeners = listOf(request.messageListener)
        )
        val client = Client(
            player = player,
            device = request.device,
            machine = request.machine,
            settings = request.settings,
            encryptedPass = data.encryptedPass,
            loginXteas = request.loginXteas,
            bufAllocator = request.bufAllocator
        )
        entity.coords = when (data.coords.size) {
            2 -> Coordinates(data.coords[0], data.coords[1])
            3 -> Coordinates(data.coords[0], data.coords[1], data.coords[2])
            else -> error("Invalid coordinate values: ${data.coords}.")
        }
        return ClientDeserializeResponse.Success(client)
    }

    override fun serialize(client: Client): DefaultClientData {
        val player = client.player
        val entity = player.entity
        return DefaultClientData(
            loginName = player.loginName,
            displayName = player.username,
            encryptedPass = client.encryptedPass,
            loginXteas = client.loginXteas,
            coords = intArrayOf(entity.coords.x, entity.coords.y, entity.coords.plane),
            rank = entity.rank
        )
    }

    override fun newClient(request: ClientDeserializeRequest): Client {
        val password = request.plaintTextPass ?: error("New client must have an input password.")
        val entity = PlayerEntity(
            username = request.loginName,
            rank = 0
        )
        val player = Player(
            id = PlayerId(request.loginName),
            loginName = request.loginName,
            eventBus = request.eventBus,
            actionBus = request.actionBus,
            entity = entity,
            messageListeners = listOf(request.messageListener)
        )
        val encryptedPass = encryption.encrypt(password)
        entity.coords = config.home
        return Client(
            player = player,
            device = request.device,
            machine = request.machine,
            settings = request.settings,
            encryptedPass = encryptedPass,
            loginXteas = request.loginXteas,
            bufAllocator = request.bufAllocator
        )
    }
}

data class DefaultClientData(
    val loginName: String,
    val displayName: String,
    val encryptedPass: String,
    val loginXteas: IntArray,
    val coords: IntArray,
    val rank: Int
) : ClientData {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultClientData

        if (loginName != other.loginName) return false
        if (displayName != other.displayName) return false
        if (encryptedPass != other.encryptedPass) return false
        if (!loginXteas.contentEquals(other.loginXteas)) return false
        if (!coords.contentEquals(other.coords)) return false
        if (rank != other.rank) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loginName.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + encryptedPass.hashCode()
        result = 31 * result + loginXteas.contentHashCode()
        result = 31 * result + coords.contentHashCode()
        result = 31 * result + rank
        return result
    }
}
