package gg.rsmod.plugins.content.xtea

import gg.rsmod.game.model.domain.repo.XteaRepository

class XteaInMemoryRepository : XteaRepository {

    private val keys = mapOf(
        12593 to intArrayOf(379171354, -1403510972, 1758621950, 406280958),
        12594 to intArrayOf(1571084498, -870324768, 508828051, 1172793559),
        12850 to intArrayOf(-1277378597, -633535097, -1589229309, 964213147),
        12849 to intArrayOf(-1745330859, -429918039, 1471319756, -1633431864),

        12336 to intArrayOf(-859942543, -1401589853, -667905731, 1178970878),
        12592 to intArrayOf(703834017, 1934081790, 1496867928, -1451878403),
        12436 to intArrayOf(1382272724, -1660013578, -148661629, -136544360),
        12180 to intArrayOf(-1351764866, -1935425725, 1882489875, 1174127151),

        12079 to intArrayOf(-2094201476, 174282827, -512716806, 1168716986),
        12080 to intArrayOf(-1275219644, 1117212890, -1700052380, 1083433768),
        12335 to intArrayOf(437353829, 1996799724, 1671468525, -157682249),
        12336 to intArrayOf(-859942543, -1401589853, -667905731, 1178970878),
        12592 to intArrayOf(703834017, 1934081790, 1496867928, -1451878403)
    )

    override fun findAll(): Collection<IntArray> = keys.values

    override fun findById(id: Int): IntArray? = keys[id]

    override fun insert(entity: IntArray, id: Int) = throw UnsupportedOperationException()

    override fun update(entity: IntArray, id: Int) = throw UnsupportedOperationException()

    override fun delete(entity: IntArray, id: Int) = throw UnsupportedOperationException()
}
