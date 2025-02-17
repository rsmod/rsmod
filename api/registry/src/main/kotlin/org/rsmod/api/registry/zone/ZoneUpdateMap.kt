package org.rsmod.api.registry.zone

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.rsprot.protocol.message.ZoneProt
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class ZoneUpdateMap {
    public val updatedZones: Int2ObjectOpenHashMap<ZoneUpdateList> = Int2ObjectOpenHashMap()

    public fun clear() {
        updatedZones.clear()
    }

    public fun locAdd(loc: LocInfo) {
        val updates = loc.coords.getOrPutUpdateList()
        val prot = ZoneUpdateTransformer.toLocAddChangeProt(loc)
        updates += prot
    }

    public fun locDel(loc: LocInfo) {
        val updates = loc.coords.getOrPutUpdateList()
        val prot = ZoneUpdateTransformer.toLocDelProt(loc)
        updates += prot
    }

    public fun locAnim(loc: LocInfo, seq: Int) {
        val updates = loc.coords.getOrPutUpdateList()
        val prot = ZoneUpdateTransformer.toLocAnim(loc, seq)
        updates += prot
    }

    public fun objAdd(obj: Obj) {
        val updates = obj.coords.getOrPutUpdateList()
        val prot = obj.resolveObjAddProt()
        updates += prot
    }

    private fun Obj.resolveObjAddProt(): ZoneProt =
        if (isPublic) {
            ZoneUpdateTransformer.toPublicObjAddProt(this)
        } else {
            ZoneUpdateTransformer.toPrivateObjAddProt(this)
        }

    public fun objDel(obj: Obj) {
        val updates = obj.coords.getOrPutUpdateList()
        val prot = obj.resolveObjDelProt()
        updates += prot
    }

    private fun Obj.resolveObjDelProt(): ZoneProt =
        if (isPublic) {
            ZoneUpdateTransformer.toPublicObjDelProt(this)
        } else {
            ZoneUpdateTransformer.toPrivateObjDelProt(this)
        }

    public fun objCount(obj: Obj, newCount: Int, oldCount: Int) {
        val updates = obj.coords.getOrPutUpdateList()
        val prot =
            ZoneUpdateTransformer.toObjCountProt(obj, oldCount = oldCount, newCount = newCount)
        updates += prot
    }

    public fun objReveal(obj: Obj) {
        val updates = obj.coords.getOrPutUpdateList()
        val prot = ZoneUpdateTransformer.toObjRevealProt(obj)
        updates += prot
    }

    public fun soundArea(
        source: CoordGrid,
        synth: Int,
        delay: Int,
        loops: Int,
        radius: Int,
        size: Int,
    ) {
        val updates = source.getOrPutUpdateList()
        val prot = ZoneUpdateTransformer.toSoundAreaProt(source, synth, delay, loops, radius, size)
        updates += prot
    }

    public operator fun get(zone: Int): ZoneUpdateList? = updatedZones.getOrDefault(zone, null)

    public operator fun get(zone: ZoneKey): ZoneUpdateList? =
        updatedZones.getOrDefault(zone.packed, null)

    private fun CoordGrid.getOrPutUpdateList(): ZoneUpdateList =
        updatedZones.computeIfAbsent(ZoneKey.from(this).packed) { ZoneUpdateList() }
}
