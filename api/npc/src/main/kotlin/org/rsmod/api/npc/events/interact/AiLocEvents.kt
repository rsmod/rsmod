package org.rsmod.api.npc.events.interact

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType

public sealed class AiLocEvents {
    public sealed class Op(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        OpEvent(type.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public sealed class Ap(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        ApEvent(type.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)
}

public sealed class AiLocContentEvents {
    public sealed class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        content: Int,
    ) : OpEvent(content.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, content)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, content)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, content)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, content)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, content)

    public sealed class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        content: Int,
    ) : ApEvent(content.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, content)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, content)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, content)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, content)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, content)
}

public sealed class AiLocDefaultEvents {
    public sealed class Op(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        OpDefaultEvent()

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public sealed class Ap(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        ApEvent(type.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)
}

public sealed class AiLocUnimplementedEvents {
    public sealed class Op(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        OpEvent(type.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)
}

public class AiLocTEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : OpEvent((type.id.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : ApEvent((type.id.toLong() shl 32) or component.packed.toLong())
}

public class AiLocTContentEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
        locContent: Int = type.contentGroup,
    ) : OpEvent((locContent.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
        locContent: Int = type.contentGroup,
    ) : ApEvent((locContent.toLong() shl 32) or component.packed.toLong())
}

public class AiLocTDefaultEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: ObjType?,
        public val comsub: Int,
        component: ComponentType,
    ) : ApEvent(component.packed.toLong())
}

public class AiLocUEvents {
    public class Op(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : OpEvent((type.id.toLong() shl 32) or objType.id.toLong())

    public class Ap(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : ApEvent((type.id.toLong() shl 32) or objType.id.toLong())
}

public class AiLocUContentEvents {
    public class OpType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : OpEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class ApType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : ApEvent((locContent.toLong() shl 32) or objType.id.toLong())

    public class OpContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        objContent: Int = objType.contentGroup,
        locContent: Int = type.contentGroup,
    ) : OpEvent((locContent.toLong() shl 32) or objContent.toLong())

    public class ApContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        objContent: Int = objType.contentGroup,
        locContent: Int = type.contentGroup,
    ) : ApEvent((locContent.toLong() shl 32) or objContent.toLong())
}

public class AiLocUDefaultEvents {
    public class OpType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : OpEvent(type.id.toLong())

    public class ApType(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
    ) : ApEvent(type.id.toLong())

    public class OpContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : OpEvent(locContent.toLong())

    public class ApContent(
        public val loc: BoundLocInfo,
        public val type: UnpackedLocType,
        public val objType: UnpackedObjType,
        public val invSlot: Int,
        locContent: Int = type.contentGroup,
    ) : ApEvent(locContent.toLong())
}
