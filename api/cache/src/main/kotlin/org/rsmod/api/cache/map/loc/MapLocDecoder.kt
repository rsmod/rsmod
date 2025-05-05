package org.rsmod.api.cache.map.loc

import it.unimi.dsi.fastutil.longs.LongArrayList
import org.rsmod.api.cache.util.InlineByteBuf

public object MapLocDecoder {
    public fun decode(buf: InlineByteBuf): MapLocDefinition {
        val locs = LongArrayList(buf.backing.size * 5 / 2)
        var cursor = buf.newCursor()
        var currLocId = -1
        while (buf.isReadable(cursor)) {
            cursor = buf.readIncrShortSmart(cursor)
            val offset = cursor.value
            if (offset == 0) break
            currLocId += offset
            var localCoords = 0
            while (buf.isReadable(cursor)) {
                cursor = buf.readShortSmart(cursor)
                val diff = cursor.value
                if (diff == 0) break
                cursor = buf.readByte(cursor)
                val attribs = cursor.value
                localCoords += diff - 1
                locs += MapLoc(currLocId, localCoords, attribs).packed
            }
        }
        return MapLocDefinition(locs)
    }
}
