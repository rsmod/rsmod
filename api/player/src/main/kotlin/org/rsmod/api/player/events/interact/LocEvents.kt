package org.rsmod.api.player.events.interact

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.VarpType

public data class MultiLocEvent(public val varp: VarpType?, public val varbit: VarBitType?)

public sealed class LocEvents {
    public sealed class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
    ) : OpEvent(type.id.toLong())

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
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
    ) : ApEvent(type.id.toLong())

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
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        content: Int,
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
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        content: Int,
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
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
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
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
    ) : ApEvent(type.id.toLong())

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
    public sealed class Op(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        OpEvent(type.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)
}

public class LocTEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
    ) : OpEvent((type.id.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
    ) : ApEvent((type.id.toLong() shl 32) or component.packed.toLong())
}

public class LocTContentEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        locContent: Int,
    ) : OpEvent((locContent.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        locContent: Int,
    ) : ApEvent((locContent.toLong() shl 32) or component.packed.toLong())
}

public class LocTDefaultEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
    ) : ApEvent(component.packed.toLong())
}

public class LocUEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : OpEvent((type.id.toLong() shl 32) or objType.id.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : ApEvent((type.id.toLong() shl 32) or objType.id.toLong())
}

public class LocUContentEvents {
    public class OpType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
    ) : OpEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class ApType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
    ) : ApEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class OpContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        objContent: Int,
        locContent: Int,
    ) : OpEvent((locContent.toLong() shl 32) or objContent.toLong())

    public class ApContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        objContent: Int,
        locContent: Int,
    ) : ApEvent((locContent.toLong() shl 32) or objContent.toLong())
}

public class LocUDefaultEvents {
    public class OpType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : OpEvent(type.id.toLong())

    public class ApType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val multi: MultiLocEvent?,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : ApEvent(type.id.toLong())

    public class OpContent(
        public val loc: BoundLocInfo,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
    ) : OpEvent(locContent.toLong())

    public class ApContent(
        public val loc: BoundLocInfo,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        locContent: Int,
    ) : ApEvent(locContent.toLong())
}
