package org.rsmod.api.player.events.interact

import org.rsmod.game.entity.Player

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
