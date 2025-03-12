package org.rsmod.api.npc.events.interact

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player

public class AiPlayerEvents {
    public sealed class Op(public val target: Player, npc: Npc) : OpEvent(npc.id.toLong())

    public class Op1(target: Player, npc: Npc) : Op(target, npc)

    public class Op2(target: Player, npc: Npc) : Op(target, npc)

    public class Op3(target: Player, npc: Npc) : Op(target, npc)

    public class Op4(target: Player, npc: Npc) : Op(target, npc)

    public class Op5(target: Player, npc: Npc) : Op(target, npc)

    public sealed class Ap(public val target: Player, npc: Npc) : ApEvent(npc.id.toLong())

    public class Ap1(target: Player, npc: Npc) : Ap(target, npc)

    public class Ap2(target: Player, npc: Npc) : Ap(target, npc)

    public class Ap3(target: Player, npc: Npc) : Ap(target, npc)

    public class Ap4(target: Player, npc: Npc) : Ap(target, npc)

    public class Ap5(target: Player, npc: Npc) : Ap(target, npc)
}

public class AiPlayerDefaultEvents {
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
