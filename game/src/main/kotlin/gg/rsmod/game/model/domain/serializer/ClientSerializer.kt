package gg.rsmod.game.model.domain.serializer

import gg.rsmod.game.message.MessageListener
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.ClientMachine
import gg.rsmod.game.model.client.ClientSettings

data class ClientDeserializeRequest(
    val loginName: String,
    val settings: ClientSettings,
    val machine: ClientMachine,
    val messageListener: MessageListener
)

interface ClientSerializer {
    fun deserialize(request: ClientDeserializeRequest): Client
    fun serialize(client: Client)
}
