package gg.rsmod.plugins.core.serializer

import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.PlayerEntity
import gg.rsmod.game.model.domain.PlayerId
import gg.rsmod.game.model.domain.serializer.ClientData
import gg.rsmod.game.model.domain.serializer.ClientDataMapper
import gg.rsmod.game.model.domain.serializer.ClientDeserializeRequest
import gg.rsmod.game.model.mob.Player
import kotlin.reflect.KClass

class DefaultClientMapper : ClientDataMapper<DefaultClientData> {

    override fun deserialize(request: ClientDeserializeRequest, data: DefaultClientData): Client {
        val entity = PlayerEntity(
            username = data.displayName,
            privilege = data.privilege
        )
        val player = Player(
            id = PlayerId(data.loginName),
            loginName = data.loginName,
            entity = entity,
            messageListeners = listOf(request.messageListener)
        )
        return Client(
            player = player,
            machine = request.machine,
            settings = request.settings,
            encryptedPass = data.encryptedPass,
            loginXteas = data.loginXteas
        )
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
            privilege = entity.privilege
        )
    }

    override fun type(): KClass<DefaultClientData> {
        return DefaultClientData::class
    }
}

data class DefaultClientData(
    val loginName: String,
    val displayName: String,
    val encryptedPass: String,
    val loginXteas: IntArray,
    val coords: IntArray,
    val privilege: Int
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
        if (privilege != other.privilege) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loginName.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + encryptedPass.hashCode()
        result = 31 * result + loginXteas.contentHashCode()
        result = 31 * result + coords.contentHashCode()
        result = 31 * result + privilege
        return result
    }
}
