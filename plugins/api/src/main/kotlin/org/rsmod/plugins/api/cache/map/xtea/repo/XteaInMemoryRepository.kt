package org.rsmod.plugins.api.cache.map.xtea.repo

import org.rsmod.game.model.domain.repo.XteaRepository

class XteaInMemoryRepository : XteaRepository {

    private val keys = mutableMapOf<Int, IntArray>()

    override fun entries(): Map<Int, IntArray> = keys

    override fun keys(): Collection<Int> = keys.keys

    override fun values(): Collection<IntArray> = keys.values

    override fun findById(id: Int): IntArray? = keys[id]

    override fun insert(entity: IntArray, id: Int) {
        keys[id] = entity
    }

    override fun update(entity: IntArray, id: Int) = throw UnsupportedOperationException()

    override fun delete(entity: IntArray, id: Int) = throw UnsupportedOperationException()
}
