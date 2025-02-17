package org.rsmod.api.player.events.interact

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType

public data class MultiLocEvent(public val varp: VarpType?, public val varbit: VarBitType?)

public sealed class LocEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(loc.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public sealed class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent(loc.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)
}

public sealed class LocContentEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        content: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(content.toLong())

    public class Op1(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Op(loc, type, multi, content)

    public class Op2(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Op(loc, type, multi, content)

    public class Op3(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Op(loc, type, multi, content)

    public class Op4(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Op(loc, type, multi, content)

    public class Op5(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Op(loc, type, multi, content)

    public sealed class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        content: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent(content.toLong())

    public class Ap1(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Ap(loc, type, multi, content)

    public class Ap2(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Ap(loc, type, multi, content)

    public class Ap3(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Ap(loc, type, multi, content)

    public class Ap4(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Ap(loc, type, multi, content)

    public class Ap5(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        multi: MultiLocEvent?,
        content: Int,
    ) : Ap(loc, type, multi, content)
}

public sealed class LocDefaultEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpDefaultEvent()

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Op(loc, type, multi)

    public sealed class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent(loc.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, multi: MultiLocEvent?) :
        Ap(loc, type, multi)
}

public sealed class LocUnimplementedEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(loc.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)
}

public class LocTEvents {
    public class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent((type.id.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent((type.id.toLong() shl 32) or component.packed.toLong())
}

public class LocTContentEvents {
    public class Op(
        public val bound: BoundLocInfo,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent((locContent.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val bound: BoundLocInfo,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent((locContent.toLong() shl 32) or component.packed.toLong())
}

public class LocTDefaultEvents {
    public class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent(component.packed.toLong())
}

public class LocUEvents {
    public class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent((type.id.toLong() shl 32) or objType.id.toLong())

    public class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent((type.id.toLong() shl 32) or objType.id.toLong())
}

public class LocUContentEvents {
    public class OpType(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class ApType(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class OpContent(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        objContent: Int,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent((locContent.toLong() shl 32) or objContent.toLong())

    public class ApContent(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        objContent: Int,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent((locContent.toLong() shl 32) or objContent.toLong())
}

public class LocUDefaultEvents {
    public class OpType(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(type.id.toLong())

    public class ApType(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent(type.id.toLong())

    public class OpContent(
        public val bound: BoundLocInfo,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(locContent.toLong())

    public class ApContent(
        public val bound: BoundLocInfo,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : ApEvent(locContent.toLong())
}

private fun BoundLocInfo.toLocInfo(): LocInfo = LocInfo(layer, coords, entity)
