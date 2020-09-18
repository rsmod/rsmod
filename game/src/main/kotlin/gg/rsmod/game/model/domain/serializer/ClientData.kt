package gg.rsmod.game.model.domain.serializer

import gg.rsmod.game.model.client.Client
import kotlin.reflect.KClass

interface ClientData

interface ClientDataMapper<T : ClientData> {
    fun deserialize(request: ClientDeserializeRequest, data: T): Client
    fun serialize(client: Client): T
    fun type(): KClass<T>
}
