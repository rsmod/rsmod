package org.rsmod.api.net.rsprot

import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.nio.file.Paths
import net.rsprot.compression.provider.HuffmanCodecProvider
import net.rsprot.crypto.rsa.RsaKeyPair
import net.rsprot.protocol.api.AbstractNetworkServiceFactory
import net.rsprot.protocol.api.GameConnectionHandler
import net.rsprot.protocol.api.handlers.ExceptionHandlers
import net.rsprot.protocol.api.js5.Js5GroupProvider
import net.rsprot.protocol.api.suppliers.NpcInfoSupplier
import net.rsprot.protocol.api.suppliers.WorldEntityInfoSupplier
import net.rsprot.protocol.common.client.OldSchoolClientType
import net.rsprot.protocol.message.codec.incoming.provider.GameMessageConsumerRepositoryProvider
import org.openrs2.cache.Store
import org.rsmod.annotations.Js5Cache
import org.rsmod.api.net.rsprot.provider.ExceptionHandlersProvider
import org.rsmod.api.net.rsprot.provider.HuffmanProvider
import org.rsmod.api.net.rsprot.provider.Js5GroupResponseProvider
import org.rsmod.api.net.rsprot.provider.Js5Store
import org.rsmod.api.net.rsprot.provider.MessageConsumerProvider
import org.rsmod.api.net.rsprot.provider.NpcSupplier
import org.rsmod.api.net.rsprot.provider.RsaProvider
import org.rsmod.api.net.rsprot.provider.WorldEntityProvider
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList

@OptIn(ExperimentalUnsignedTypes::class)
@Singleton
class NetworkFactory
@Inject
constructor(
    @Js5Cache private val store: Store,
    private val messageConsumerProvider: MessageConsumerProvider,
    private val huffmanProvider: HuffmanProvider,
    players: PlayerList,
    events: EventBus,
) : AbstractNetworkServiceFactory<Player>() {
    private val js5Store = Js5Store.from(store)
    private val js5Groups = Js5GroupResponseProvider(js5Store)
    private val connectionHandler = ConnectionHandler(players, events)
    private val npcSupplier = NpcSupplier.provide()

    override val ports: List<Int> = listOf(43594)

    override val supportedClientTypes: List<OldSchoolClientType> =
        listOf(OldSchoolClientType.DESKTOP)

    override fun getExceptionHandlers(): ExceptionHandlers<Player> {
        return ExceptionHandlersProvider.provide()
    }

    override fun getGameConnectionHandler(): GameConnectionHandler<Player> {
        return connectionHandler
    }

    override fun getGameMessageConsumerRepositoryProvider():
        GameMessageConsumerRepositoryProvider<Player> {
        return messageConsumerProvider.get()
    }

    override fun getHuffmanCodecProvider(): HuffmanCodecProvider {
        return huffmanProvider.provide()
    }

    override fun getJs5GroupProvider(): Js5GroupProvider {
        return js5Groups
    }

    override fun getNpcInfoSupplier(): NpcInfoSupplier {
        return npcSupplier
    }

    override fun getRsaKeyPair(): RsaKeyPair {
        return RsaProvider.from(Paths.get(".data", "game.key"))
    }

    override fun getWorldEntityInfoSupplier(): WorldEntityInfoSupplier {
        return WorldEntityProvider.provide()
    }
}
