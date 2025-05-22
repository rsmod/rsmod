package org.rsmod.api.player.events.interact

import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType

public class PlayerEvents {
    public sealed class Op(public val target: Player) : OpDefaultEvent()

    public class Op1(target: Player) : Op(target)

    public class Op2(target: Player) : Op(target)

    public class Op3(target: Player) : Op(target)

    public class Op4(target: Player) : Op(target)

    public class Op5(target: Player) : Op(target)

    public sealed class Ap(public val target: Player) : ApDefaultEvent()

    public class Ap1(target: Player) : Ap(target)

    public class Ap2(target: Player) : Ap(target)

    public class Ap3(target: Player) : Ap(target)

    public class Ap4(target: Player) : Ap(target)

    public class Ap5(target: Player) : Ap(target)
}

public class PlayerTEvents {
    public class Op(
        public val target: Player,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val target: Player,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
    ) : ApEvent(component.packed.toLong())
}

public class PlayerUEvents {
    public class Op(
        public val target: Player,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : OpEvent(objType.id.toLong())

    public class Ap(
        public val target: Player,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : ApEvent(objType.id.toLong())
}

public class PlayerUContentEvents {
    public class Op(
        public val target: Player,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : OpEvent(objType.contentGroup.toLong())

    public class Ap(
        public val target: Player,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : ApEvent(objType.contentGroup.toLong())
}
