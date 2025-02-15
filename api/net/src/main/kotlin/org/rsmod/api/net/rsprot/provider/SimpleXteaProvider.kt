package org.rsmod.api.net.rsprot.provider

import jakarta.inject.Inject
import net.rsprot.crypto.xtea.XteaKey
import net.rsprot.protocol.game.outgoing.map.util.XteaProvider
import org.rsmod.game.map.xtea.XteaMap

class SimpleXteaProvider @Inject constructor(private val xtea: XteaMap) : XteaProvider {
    private val keys: Map<Int, XteaKey> by lazy { xtea.toIntMap() }

    override fun provide(mapsquareId: Int): XteaKey {
        return keys.getOrDefault(mapsquareId, null) ?: XteaKey.ZERO
    }

    private fun XteaMap.toIntMap(): Map<Int, XteaKey> =
        entries.associate { it.key.id to XteaKey(it.value) }
}
