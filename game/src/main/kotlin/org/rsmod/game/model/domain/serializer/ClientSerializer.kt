package org.rsmod.game.model.domain.serializer

import io.netty.buffer.ByteBufAllocator
import org.rsmod.game.action.ActionBus
import org.rsmod.game.event.EventBus
import org.rsmod.game.message.ServerPacketListener
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.client.ClientDevice
import org.rsmod.game.model.client.ClientMachine
import org.rsmod.game.model.client.ClientSettings

interface ClientSerializer {

    fun deserialize(request: ClientDeserializeRequest): ClientDeserializeResponse

    fun serialize(client: Client)
}

sealed class ClientDeserializeResponse {
    class Success(val client: Client, val newAccount: Boolean = false) : ClientDeserializeResponse()
    object BadCredentials : ClientDeserializeResponse()
    object ReadError : ClientDeserializeResponse()
    override fun toString(): String = javaClass.simpleName
}

data class ClientDeserializeRequest(
    val loginName: String,
    val device: ClientDevice,
    val plaintTextPass: String?,
    val loginXteas: IntArray,
    val reconnectXteas: IntArray?,
    val settings: ClientSettings,
    val machine: ClientMachine,
    val messageListener: ServerPacketListener,
    val bufAllocator: ByteBufAllocator,
    val eventBus: EventBus,
    val actionBus: ActionBus
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientDeserializeRequest

        if (loginName != other.loginName) return false
        if (device != other.device) return false
        if (plaintTextPass != other.plaintTextPass) return false
        if (!loginXteas.contentEquals(other.loginXteas)) return false
        if (reconnectXteas != null) {
            if (other.reconnectXteas == null) return false
            if (!reconnectXteas.contentEquals(other.reconnectXteas)) return false
        } else if (other.reconnectXteas != null) return false
        if (settings != other.settings) return false
        if (machine != other.machine) return false
        if (messageListener != other.messageListener) return false
        if (bufAllocator != other.bufAllocator) return false
        if (eventBus != other.eventBus) return false
        if (actionBus != other.actionBus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loginName.hashCode()
        result = 31 * result + device.hashCode()
        result = 31 * result + (plaintTextPass?.hashCode() ?: 0)
        result = 31 * result + loginXteas.contentHashCode()
        result = 31 * result + (reconnectXteas?.contentHashCode() ?: 0)
        result = 31 * result + settings.hashCode()
        result = 31 * result + machine.hashCode()
        result = 31 * result + messageListener.hashCode()
        result = 31 * result + bufAllocator.hashCode()
        result = 31 * result + eventBus.hashCode()
        result = 31 * result + actionBus.hashCode()
        return result
    }
}
