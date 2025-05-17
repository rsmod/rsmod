package org.rsmod.api.cache.map.loc

import it.unimi.dsi.fastutil.longs.LongArrayList
import org.rsmod.api.cache.util.InlineByteBuf

public object MapLocListDecoder {
    public fun decode(buf: InlineByteBuf): MapLocListDefinition {
        val locs = LongArrayList(buf.backing.size * 5 / 2)
        var cursor = buf.newCursor()
        var currLocId = -1
        while (buf.isReadable(cursor)) {
            cursor = buf.readIncrShortSmart(cursor)
            val offset = cursor.value
            if (offset == 0) {
                break
            }
            currLocId += offset
            var localCoords = 0
            while (buf.isReadable(cursor)) {
                cursor = buf.readShortSmart(cursor)
                val diff = cursor.value
                if (diff == 0) {
                    break
                }
                localCoords += diff - 1
                cursor = buf.readByte(cursor)
                val attribs = cursor.value
                locs += MapLocDefinition(currLocId, localCoords, attribs).packed
            }
        }
        return MapLocListDefinition(locs)
    }
}
