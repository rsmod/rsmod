package org.rsmod.api.player.events.interact

import org.rsmod.game.entity.Npc
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType

public sealed class NpcEvents {
    public sealed class Op(public val npc: Npc) : OpEvent(npc.id.toLong())

    public class Op1(npc: Npc) : Op(npc)

    public class Op2(npc: Npc) : Op(npc)

    public class Op3(npc: Npc) : Op(npc)

    public class Op4(npc: Npc) : Op(npc)

    public class Op5(npc: Npc) : Op(npc)

    public sealed class Ap(public val npc: Npc) : ApEvent(npc.id.toLong())

    public class Ap1(npc: Npc) : Ap(npc)

    public class Ap2(npc: Npc) : Ap(npc)

    public class Ap3(npc: Npc) : Ap(npc)

    public class Ap4(npc: Npc) : Ap(npc)

    public class Ap5(npc: Npc) : Ap(npc)
}

public sealed class NpcContentEvents {
    public sealed class Op(public val npc: Npc, contentGroup: Int) : OpEvent(contentGroup.toLong())

    public class Op1(npc: Npc, category: Int) : Op(npc, category)

    public class Op2(npc: Npc, category: Int) : Op(npc, category)

    public class Op3(npc: Npc, category: Int) : Op(npc, category)

    public class Op4(npc: Npc, category: Int) : Op(npc, category)

    public class Op5(npc: Npc, category: Int) : Op(npc, category)

    public sealed class Ap(public val npc: Npc, contentGroup: Int) : ApEvent(contentGroup.toLong())

    public class Ap1(npc: Npc, category: Int) : Ap(npc, category)

    public class Ap2(npc: Npc, category: Int) : Ap(npc, category)

    public class Ap3(npc: Npc, category: Int) : Ap(npc, category)

    public class Ap4(npc: Npc, category: Int) : Ap(npc, category)

    public class Ap5(npc: Npc, category: Int) : Ap(npc, category)
}

public sealed class NpcDefaultEvents {
    public sealed class Op(public val npc: Npc) : OpDefaultEvent()

    public class Op1(npc: Npc) : Op(npc)

    public class Op2(npc: Npc) : Op(npc)

    public class Op3(npc: Npc) : Op(npc)

    public class Op4(npc: Npc) : Op(npc)

    public class Op5(npc: Npc) : Op(npc)

    public sealed class Ap(public val npc: Npc) : ApDefaultEvent()

    public class Ap1(npc: Npc) : Ap(npc)

    public class Ap2(npc: Npc) : Ap(npc)

    public class Ap3(npc: Npc) : Ap(npc)

    public class Ap4(npc: Npc) : Ap(npc)

    public class Ap5(npc: Npc) : Ap(npc)
}

public sealed class NpcUnimplementedEvents {
    public sealed class Op(public val npc: Npc) : OpEvent(npc.id.toLong())

    public class Op1(npc: Npc) : Op(npc)

    public class Op2(npc: Npc) : Op(npc)

    public class Op3(npc: Npc) : Op(npc)

    public class Op4(npc: Npc) : Op(npc)

    public class Op5(npc: Npc) : Op(npc)
}

public class NpcTEvents {
    public class Op(
        public val npc: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        npcType: UnpackedNpcType,
        component: ComponentType,
    ) : OpEvent((npcType.id.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val npc: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        npcType: UnpackedNpcType,
        component: ComponentType,
    ) : ApEvent((npcType.id.toLong() shl 32) or component.packed.toLong())
}

public class NpcTContentEvents {
    public class Op(
        public val npc: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        content: Int,
    ) : OpEvent((content.toLong() shl 32) or component.packed.toLong())

    public class Ap(
        public val npc: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        content: Int,
    ) : ApEvent((content.toLong() shl 32) or component.packed.toLong())
}

public class NpcTDefaultEvents {
    public class Op(
        public val npc: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        public val npcType: UnpackedNpcType,
        component: ComponentType,
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val npc: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        public val npcType: UnpackedNpcType,
        component: ComponentType,
    ) : ApEvent(component.packed.toLong())
}

public class NpcUEvents {
    public class Op(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcType: UnpackedNpcType,
    ) : OpEvent((npcType.id.toLong() shl 32) or objType.id.toLong())

    public class Ap(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcType: UnpackedNpcType,
    ) : ApEvent((npcType.id.toLong() shl 32) or objType.id.toLong())
}

public class NpcUContentEvents {
    public class Op(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : OpEvent((content.toLong() shl 32) or objType.id.toLong())

    public class Ap(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : ApEvent((content.toLong() shl 32) or objType.id.toLong())
}

public class NpcUDefaultEvents {
    public class OpType(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : OpEvent(npc.id.toLong())

    public class ApType(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
    ) : ApEvent(npc.id.toLong())

    public class OpContent(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : OpEvent(content.toLong())

    public class ApContent(
        public val npc: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : ApEvent(content.toLong())
}
