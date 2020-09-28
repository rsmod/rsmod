package gg.rsmod.game.model.domain.serializer

import gg.rsmod.game.message.MessageListener
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.ClientMachine
import gg.rsmod.game.model.client.ClientSettings

interface ClientSerializer {

    fun deserialize(request: ClientDeserializeRequest): ClientDeserializeResponse

    fun serialize(client: Client)
}

sealed class ClientDeserializeResponse {
    class Success(val client: Client) : ClientDeserializeResponse()
    object BadCredentials : ClientDeserializeResponse()
    object ReadError : ClientDeserializeResponse()
    override fun toString(): String = javaClass.simpleName
}

data class ClientDeserializeRequest(
    val loginName: String,
    val plaintTextPass: String?,
    val loginXteas: IntArray,
    val reconnectXteas: IntArray?,
    val settings: ClientSettings,
    val machine: ClientMachine,
    val messageListener: MessageListener
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientDeserializeRequest

        if (loginName != other.loginName) return false
        if (plaintTextPass != other.plaintTextPass) return false
        if (!loginXteas.contentEquals(other.loginXteas)) return false
        if (reconnectXteas != null) {
            if (other.reconnectXteas == null) return false
            if (!reconnectXteas.contentEquals(other.reconnectXteas)) return false
        } else if (other.reconnectXteas != null) return false
        if (settings != other.settings) return false
        if (machine != other.machine) return false
        if (messageListener != other.messageListener) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loginName.hashCode()
        result = 31 * result + (plaintTextPass?.hashCode() ?: 0)
        result = 31 * result + loginXteas.contentHashCode()
        result = 31 * result + (reconnectXteas?.contentHashCode() ?: 0)
        result = 31 * result + settings.hashCode()
        result = 31 * result + machine.hashCode()
        result = 31 * result + messageListener.hashCode()
        return result
    }
}
