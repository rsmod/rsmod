package org.rsmod.api.player.events.interact

import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.UnpackedObjType

public sealed class WornObjEvents(id: Int) : OpEvent(id.toLong()) {
    public class Op1(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op2(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op3(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op4(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op5(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op6(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op7(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op8(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)

    public class Op9(public val slot: Int, public val obj: InvObj) : WornObjEvents(obj.id)
}

public sealed class WornObjContentEvents(id: Int) : OpEvent(id.toLong()) {
    public class Op1(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op2(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op3(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op4(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op5(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op6(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op7(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op8(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)

    public class Op9(public val slot: Int, public val obj: InvObj, type: UnpackedObjType) :
        WornObjEvents(type.contentGroup)
}
