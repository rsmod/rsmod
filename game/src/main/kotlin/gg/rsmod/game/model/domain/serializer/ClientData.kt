package gg.rsmod.game.model.domain.serializer

import gg.rsmod.game.model.client.Client
import kotlin.reflect.KClass

interface ClientData

interface ClientDataMapper<T : ClientData> {

    fun deserialize(request: ClientDeserializeRequest, data: T): ClientDeserializeResponse

    fun serialize(client: Client): T

    fun newClient(request: ClientDeserializeRequest): Client

    fun type(): KClass<T>
}
