package org.rsmod.api.registry.zone

import net.rsprot.protocol.message.ZoneProt

public class ZoneUpdateList : Iterable<ZoneProt> {
    public val protList: ArrayDeque<ZoneProt> = ArrayDeque()

    public val size: Int
        get() = protList.size

    public val isEmpty: Boolean
        get() = protList.isEmpty()

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public operator fun plusAssign(prot: ZoneProt) {
        protList += prot
    }

    override fun iterator(): Iterator<ZoneProt> = protList.iterator()
}
