package org.rsmod.api.npc.events.interact

import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType

public sealed class AiNpcEvents {
    public sealed class Op(public val target: Npc) : OpEvent(target.id.toLong())

    public class Op1(target: Npc) : Op(target)

    public class Op2(target: Npc) : Op(target)

    public class Op3(target: Npc) : Op(target)

    public class Op4(target: Npc) : Op(target)

    public class Op5(target: Npc) : Op(target)

    public sealed class Ap(public val target: Npc) : ApEvent(target.id.toLong())

    public class Ap1(target: Npc) : Ap(target)

    public class Ap2(target: Npc) : Ap(target)

    public class Ap3(target: Npc) : Ap(target)

    public class Ap4(target: Npc) : Ap(target)

    public class Ap5(target: Npc) : Ap(target)
}

public sealed class AiNpcContentEvents {
    public sealed class Op(public val target: Npc, contentGroup: Int) :
        OpEvent(contentGroup.toLong())

    public class Op1(target: Npc, category: Int) : Op(target, category)

    public class Op2(target: Npc, category: Int) : Op(target, category)

    public class Op3(target: Npc, category: Int) : Op(target, category)

    public class Op4(target: Npc, category: Int) : Op(target, category)

    public class Op5(target: Npc, category: Int) : Op(target, category)

    public sealed class Ap(public val target: Npc, contentGroup: Int) :
        ApEvent(contentGroup.toLong())

    public class Ap1(target: Npc, category: Int) : Ap(target, category)

    public class Ap2(target: Npc, category: Int) : Ap(target, category)

    public class Ap3(target: Npc, category: Int) : Ap(target, category)

    public class Ap4(target: Npc, category: Int) : Ap(target, category)

    public class Ap5(target: Npc, category: Int) : Ap(target, category)
}

public sealed class AiNpcDefaultEvents {
    public sealed class Op(public val target: Npc) : OpDefaultEvent()

    public class Op1(target: Npc) : Op(target)

    public class Op2(target: Npc) : Op(target)

    public class Op3(target: Npc) : Op(target)

    public class Op4(target: Npc) : Op(target)

    public class Op5(target: Npc) : Op(target)

    public sealed class Ap(public val target: Npc) : ApDefaultEvent()

    public class Ap1(target: Npc) : Ap(target)

    public class Ap2(target: Npc) : Ap(target)

    public class Ap3(target: Npc) : Ap(target)

    public class Ap4(target: Npc) : Ap(target)

    public class Ap5(target: Npc) : Ap(target)
}

public sealed class AiNpcUnimplementedEvents {
    public sealed class Op(public val target: Npc) : OpEvent(target.id.toLong())

    public class Op1(target: Npc) : Op(target)

    public class Op2(target: Npc) : Op(target)

    public class Op3(target: Npc) : Op(target)

    public class Op4(target: Npc) : Op(target)

    public class Op5(target: Npc) : Op(target)
}

public class AiNpcTEvents {
    public class Op(
        public val target: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        npcType: UnpackedNpcType,
        component: ComponentType,
    ) : OpEvent(EventBus.composeLongKey(npcType.id, component.packed))

    public class Ap(
        public val target: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        npcType: UnpackedNpcType,
        component: ComponentType,
    ) : ApEvent(EventBus.composeLongKey(npcType.id, component.packed))
}

public class AiNpcTContentEvents {
    public class Op(
        public val target: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        content: Int,
    ) : OpEvent(EventBus.composeLongKey(content, component.packed))

    public class Ap(
        public val target: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        content: Int,
    ) : ApEvent(EventBus.composeLongKey(content, component.packed))
}

public class AiNpcTDefaultEvents {
    public class Op(
        public val target: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        public val npcType: UnpackedNpcType,
        component: ComponentType,
    ) : OpEvent(component.packed.toLong())

    public class Ap(
        public val target: Npc,
        public val comsub: Int,
        public val objType: ObjType?,
        public val npcType: UnpackedNpcType,
        component: ComponentType,
    ) : ApEvent(component.packed.toLong())
}

public class AiNpcUEvents {
    public class Op(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcType: UnpackedNpcType,
    ) : OpEvent(EventBus.composeLongKey(npcType.id, objType.id))

    public class Ap(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcType: UnpackedNpcType,
    ) : ApEvent(EventBus.composeLongKey(npcType.id, objType.id))
}

public class AiNpcUContentEvents {
    public class Op(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : OpEvent(EventBus.composeLongKey(content, objType.id))

    public class Ap(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : ApEvent(EventBus.composeLongKey(content, objType.id))
}

public class AiNpcUDefaultEvents {
    public class OpType(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcType: UnpackedNpcType,
    ) : OpEvent(npcType.id.toLong())

    public class ApType(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcType: UnpackedNpcType,
    ) : ApEvent(npcType.id.toLong())

    public class OpContent(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : OpEvent(content.toLong())

    public class ApContent(
        public val target: Npc,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        content: Int,
    ) : ApEvent(content.toLong())
}

public class AiPlayerTEvents {
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

public class AiPlayerTContentEvents {
    public class Op(
        public val target: Player,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        content: Int,
    ) : OpEvent(EventBus.composeLongKey(content, component.packed))

    public class Ap(
        public val target: Player,
        public val comsub: Int,
        public val objType: ObjType?,
        component: ComponentType,
        content: Int,
    ) : ApEvent(EventBus.composeLongKey(content, component.packed))
}

public class AiPlayerUEvents {
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

public class AiPlayerUContentEvents {
    public class Op(
        public val target: Player,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcContent: Int,
    ) : OpEvent(EventBus.composeLongKey(npcContent, objType.id))

    public class Ap(
        public val target: Player,
        public val invSlot: Int,
        public val objType: UnpackedObjType,
        npcContent: Int,
    ) : ApEvent(EventBus.composeLongKey(npcContent, objType.id))
}
