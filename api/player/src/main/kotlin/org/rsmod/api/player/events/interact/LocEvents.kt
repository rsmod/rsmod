package org.rsmod.api.player.events.interact

import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.loc.UnpackedLocType

public sealed class LocEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(loc.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public sealed class Ap(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        ApEvent(loc.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)
}

public sealed class LocContentEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val loc: LocInfo = bound.toLocInfo(),
        contentType: Int,
    ) : OpEvent(contentType.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, contentType = content)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, contentType = content)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, contentType = content)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, contentType = content)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Op(loc, type, contentType = content)

    public sealed class Ap(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val loc: LocInfo = bound.toLocInfo(),
        contentType: Int,
    ) : ApEvent(contentType.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, contentType = content)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, contentType = content)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, contentType = content)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, contentType = content)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType, content: Int) :
        Ap(loc, type, contentType = content)
}

public sealed class LocDefaultEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpDefaultEvent()

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public sealed class Ap(public val loc: BoundLocInfo, public val type: UnpackedLocType) :
        ApEvent(loc.id.toLong())

    public class Ap1(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap2(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap3(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap4(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)

    public class Ap5(loc: BoundLocInfo, type: UnpackedLocType) : Ap(loc, type)
}

public sealed class LocUnimplementedEvents {
    public sealed class Op(
        public val bound: BoundLocInfo,
        public val type: UnpackedLocType,
        public val loc: LocInfo = bound.toLocInfo(),
    ) : OpEvent(loc.id.toLong())

    public class Op1(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op2(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op3(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op4(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)

    public class Op5(loc: BoundLocInfo, type: UnpackedLocType) : Op(loc, type)
}

private fun BoundLocInfo.toLocInfo(): LocInfo = LocInfo(layer, coords, entity)
