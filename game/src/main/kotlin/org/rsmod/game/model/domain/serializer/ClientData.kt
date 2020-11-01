package org.rsmod.game.model.domain.serializer

import kotlin.reflect.KClass
import org.rsmod.game.model.client.Client

interface ClientData

interface ClientDataMapper<T : ClientData> {

    val type: KClass<T>

    fun deserialize(request: ClientDeserializeRequest, data: T): ClientDeserializeResponse

    fun serialize(client: Client): T

    fun newClient(request: ClientDeserializeRequest): Client
}
