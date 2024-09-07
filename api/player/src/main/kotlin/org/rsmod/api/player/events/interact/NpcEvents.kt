package org.rsmod.api.player.events.interact

import org.rsmod.game.entity.Npc

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
    public sealed class Op(public val npc: Npc, contentType: Int) : OpEvent(contentType.toLong())

    public class Op1(npc: Npc, category: Int) : Op(npc, category)

    public class Op2(npc: Npc, category: Int) : Op(npc, category)

    public class Op3(npc: Npc, category: Int) : Op(npc, category)

    public class Op4(npc: Npc, category: Int) : Op(npc, category)

    public class Op5(npc: Npc, category: Int) : Op(npc, category)

    public sealed class Ap(public val npc: Npc, contentType: Int) : ApEvent(contentType.toLong())

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
