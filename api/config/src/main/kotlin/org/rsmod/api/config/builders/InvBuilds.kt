package org.rsmod.api.config.builders

import org.rsmod.api.config.Constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.inv.InvBuilder
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType

internal object InvBuilds : InvBuilder() {
    val generalshop1 =
        build("generalshop1") {
            scope = InvScope.Shared
            stack = InvStackType.Always
            size = Constants.shop_default_size
            restock = true
            allStock = true
            stock1 = stock(objs.pot_empty, count = 5, restockTicks = 10)
            stock2 = stock(objs.jug_empty, count = 2, restockTicks = 100)
            stock3 = stock(objs.pack_jug_empty, count = 5, restockTicks = 20)
            stock4 = stock(objs.shears, count = 2, restockTicks = 100)
            stock5 = stock(objs.knife, count = 5, restockTicks = 100)
            stock6 = stock(objs.bucket_empty, count = 3, restockTicks = 10)
            stock7 = stock(objs.pack_bucket, count = 15, restockTicks = 10)
            stock8 = stock(objs.bowl_empty, count = 2, restockTicks = 50)
            stock9 = stock(objs.cake_tin, count = 2, restockTicks = 50)
            stock10 = stock(objs.tinderbox, count = 2, restockTicks = 100)
            stock11 = stock(objs.chisel, count = 2, restockTicks = 100)
            stock12 = stock(objs.spade, count = 5, restockTicks = 100)
            stock13 = stock(objs.hammer, count = 5, restockTicks = 100)
            stock14 = stock(objs.newcomer_map, count = 5, restockTicks = 100)
            stock15 = stock(objs.sos_security_book, count = 5, restockTicks = 100)
        }
}
