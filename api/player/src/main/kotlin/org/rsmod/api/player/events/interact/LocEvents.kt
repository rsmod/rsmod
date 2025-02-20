package org.rsmod.api.player.events.interact

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType

public sealed class LocEvents {
    public sealed class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
    ) : OpEvent(type.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public sealed class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
    ) : ApEvent(type.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)
}

public sealed class LocContentEvents {
    public sealed class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        content: Int,
    ) : OpEvent(content.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Op(loc, type, base, content)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Op(loc, type, base, content)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Op(loc, type, base, content)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Op(loc, type, base, content)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Op(loc, type, base, content)

    public sealed class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        content: Int,
    ) : ApEvent(content.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Ap(loc, type, base, content)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Ap(loc, type, base, content)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Ap(loc, type, base, content)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Ap(loc, type, base, content)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo, content: Int) :
        Ap(loc, type, base, content)
}

public sealed class LocDefaultEvents {
    public sealed class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
    ) : OpDefaultEvent()

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public sealed class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
    ) : ApEvent(type.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Ap(loc, type, base)
}

public sealed class LocUnimplementedEvents {
    public sealed class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
    ) : OpEvent(type.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, base: BoundLocInfo) :
        Op(loc, type, base)
}

public class LocTEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : OpEvent((type.id.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : ApEvent((type.id.toLong() shl 32) or component.packed.toLong())
}

public class LocTContentEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
        locContent: Int = type.contentGroup,
    ) : OpEvent((locContent.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
        locContent: Int = type.contentGroup,
    ) : ApEvent((locContent.toLong() shl 32) or component.packed.toLong())
}

public class LocTDefaultEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : ApEvent(component.packed.toLong())
}

public class LocUEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : OpEvent((type.id.toLong() shl 32) or objType.id.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : ApEvent((type.id.toLong() shl 32) or objType.id.toLong())
}

public class LocUContentEvents {
    public class OpType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : OpEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class ApType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : ApEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class OpContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        objContent: Int = objType.contentGroup,
        locContent: Int = type.contentGroup,
    ) : OpEvent((locContent.toLong() shl 32) or objContent.toLong())

    public class ApContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        objContent: Int = objType.contentGroup,
        locContent: Int = type.contentGroup,
    ) : ApEvent((locContent.toLong() shl 32) or objContent.toLong())
}

public class LocUDefaultEvents {
    public class OpType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : OpEvent(type.id.toLong())

    public class ApType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : ApEvent(type.id.toLong())

    public class OpContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : OpEvent(locContent.toLong())

    public class ApContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val base: BoundLocInfo,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : ApEvent(locContent.toLong())
}
