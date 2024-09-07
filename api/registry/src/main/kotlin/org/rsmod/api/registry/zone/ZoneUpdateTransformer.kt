package org.rsmod.api.registry.zone

import net.rsprot.protocol.ServerProtCategory
import net.rsprot.protocol.game.outgoing.util.OpFlags
import net.rsprot.protocol.game.outgoing.zone.payload.LocAddChange
import net.rsprot.protocol.game.outgoing.zone.payload.LocDel
import net.rsprot.protocol.game.outgoing.zone.payload.ObjAdd
import net.rsprot.protocol.game.outgoing.zone.payload.ObjCount
import net.rsprot.protocol.game.outgoing.zone.payload.ObjDel
import net.rsprot.protocol.message.ZoneProt
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.map.zone.ZoneGrid

public object ZoneUpdateTransformer {
    public fun collectEnclosedProtList(updates: ZoneUpdateList): List<ZoneProt> = updates.protList

    public fun toPersistentLocChange(loc: LocInfo): ZoneProt {
        val zoneGrid = ZoneGrid.from(loc.coords)
        return if (loc.id == LocRegistry.DELETED_LOC_ID) {
            LocDel(zoneGrid.x, zoneGrid.z, loc.shape, loc.angle)
        } else {
            LocAddChange(loc.id, zoneGrid.x, zoneGrid.z, loc.shape, loc.angle, OpFlags.ALL_SHOWN)
        }
    }

    public fun toLocAddChangeProt(loc: LocInfo): ZoneProt {
        val zoneGrid = ZoneGrid.from(loc.coords)
        return LocAddChange(loc.id, zoneGrid.x, zoneGrid.z, loc.shape, loc.angle, OpFlags.ALL_SHOWN)
    }

    public fun toLocDelProt(loc: LocInfo): ZoneProt {
        val zoneGrid = ZoneGrid.from(loc.coords)
        return LocDel(zoneGrid.x, zoneGrid.z, loc.shape, loc.angle)
    }

    public fun toPersistentObjAdd(obj: Obj, observerId: Long?): ZoneProt? =
        if (obj.isVisibleTo(observerId)) {
            val zoneGrid = ZoneGrid.from(obj.coords)
            ObjAdd(obj.type, obj.count, zoneGrid.x, zoneGrid.z, OpFlags.ALL_SHOWN)
        } else {
            null
        }

    public fun toPublicObjAddProt(obj: Obj): ZoneProt {
        val zoneGrid = ZoneGrid.from(obj.coords)
        return ObjAdd(obj.type, obj.count, zoneGrid.x, zoneGrid.z, OpFlags.ALL_SHOWN)
    }

    public fun toPrivateObjAddProt(obj: Obj): ZoneProt {
        val zoneGrid = ZoneGrid.from(obj.coords)
        val prot = ObjAdd(obj.type, obj.count, zoneGrid.x, zoneGrid.z, OpFlags.ALL_SHOWN)
        return ObjAddPrivate(obj.copy(), prot)
    }

    public fun toPublicObjDelProt(obj: Obj): ZoneProt {
        val zoneGrid = ZoneGrid.from(obj.coords)
        return ObjDel(obj.type, obj.count, zoneGrid.x, zoneGrid.z)
    }

    public fun toPrivateObjDelProt(obj: Obj): ZoneProt {
        val zoneGrid = ZoneGrid.from(obj.coords)
        val prot = ObjDel(obj.type, obj.count, zoneGrid.x, zoneGrid.z)
        return ObjDelPrivate(obj.copy(), prot)
    }

    public fun toObjCountProt(obj: Obj, oldCount: Int, newCount: Int): ZoneProt {
        val zoneGrid = ZoneGrid.from(obj.coords)
        val prot = ObjCount(obj.type, oldCount, newCount, zoneGrid.x, zoneGrid.z)
        return ObjCountPrivate(obj.copy(), prot)
    }

    public fun toObjRevealProt(obj: Obj): ZoneProt {
        val zoneGrid = ZoneGrid.from(obj.coords)
        val prot = ObjAdd(obj.type, obj.count, zoneGrid.x, zoneGrid.z, OpFlags.ALL_SHOWN)
        return ObjReveal(obj.copy(), prot)
    }

    private fun Obj.copy(): Obj = Obj(coords, entity, creationCycle, receiverId)

    public interface PartialFollowsZoneProt : ZoneProt {
        public val backing: ZoneProt

        override val protId: Int
            get() = backing.protId

        override val category: ServerProtCategory
            get() = backing.category
    }

    public interface ObjPrivateZoneProt : PartialFollowsZoneProt {
        public val obj: Obj

        public fun isVisibleTo(observer: Long?): Boolean = obj.isVisibleTo(observer)
    }

    public class ObjAddPrivate(override val obj: Obj, override val backing: ZoneProt) :
        ObjPrivateZoneProt

    public class ObjDelPrivate(override val obj: Obj, override val backing: ZoneProt) :
        ObjPrivateZoneProt

    public class ObjCountPrivate(override val obj: Obj, override val backing: ZoneProt) :
        ObjPrivateZoneProt

    public class ObjReveal(public val obj: Obj, override val backing: ZoneProt) :
        PartialFollowsZoneProt
}
