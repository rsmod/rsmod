package org.rsmod.api.cache.map.obj

import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongLists
import org.rsmod.api.cache.util.InlineByteBuf

public object MapObjListDecoder {
    public fun decode(buf: InlineByteBuf): MapObjListDefinition {
        var cursor = buf.newCursor()

        cursor = buf.readShort(cursor)
        val count = cursor.value
        if (count == 0) {
            return MapObjListDefinition(LongLists.EMPTY_LIST)
        }

        val objs = LongArrayList(count)
        repeat(count) {
            cursor = buf.readInt(cursor)
            val high = cursor.value.toLong()

            cursor = buf.readInt(cursor)
            val low = cursor.value.toLong()

            val packed = ((high and 0xFFFFFFFFL) shl 32) or (low and 0xFFFFFFFFL)
            objs.add(packed)
        }
        return MapObjListDefinition(objs)
    }
}
