package gg.rsmod.plugins.core.xtea

import gg.rsmod.game.model.domain.repo.XteaRepository

class XteaInMemoryRepository : XteaRepository {

    private val keys = mutableMapOf(
        12593 to intArrayOf(379171354, -1403510972, 1758621950, 406280958),
        12594 to intArrayOf(1571084498, -870324768, 508828051, 1172793559),
        12850 to intArrayOf(-1277378597, -633535097, -1589229309, 964213147),
        12849 to intArrayOf(-1745330859, -429918039, 1471319756, -1633431864)
    )

    override fun findAll(): Collection<IntArray> = keys.values

    override fun findById(id: Int): IntArray? = keys[id]

    override fun insert(entity: IntArray, id: Int) = throw UnsupportedOperationException()

    override fun update(entity: IntArray, id: Int) = throw UnsupportedOperationException()

    override fun delete(entity: IntArray, id: Int) = throw UnsupportedOperationException()
}
