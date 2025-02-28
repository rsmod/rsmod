package org.rsmod.game.type.util

import java.util.BitSet
import org.rsmod.game.type.varn.UnpackedVarnType
import org.rsmod.game.type.varnbit.UnpackedVarnBitType

public object VarnpcCollisions {
    public fun detect(
        varns: Iterable<UnpackedVarnType>,
        varnbits: Iterable<UnpackedVarnBitType>,
    ): List<Error> {
        val results = mutableListOf<Error>()

        val varnsBitSets = varns.associate { it.id to BitSet() }
        for (varnbit in varnbits) {
            val varnBitSet = varnsBitSets[varnbit.baseVar.id]
            if (varnBitSet == null) {
                results += Error.InvalidBaseVar(varnbit)
                continue
            }

            val varnbitRange = varnbit.asBitSet()
            if (varnBitSet.intersects(varnbitRange)) {
                results += Error.VarnBitCollision(varnbit)
                continue
            }

            varnBitSet.or(varnbitRange)
        }

        return results
    }

    private fun UnpackedVarnBitType.asBitSet(): BitSet = BitSet().apply { set(lsb, msb) }

    public sealed class Error(public val varnbit: UnpackedVarnBitType) {
        public class InvalidBaseVar(varnbit: UnpackedVarnBitType) : Error(varnbit) {
            override fun toString(): String =
                "InvalidVarn(varn=${varnbit.baseVar.internalId}, varnbit=${varnbit.internalId})"
        }

        public class VarnBitCollision(varnbit: UnpackedVarnBitType) : Error(varnbit) {
            override fun toString(): String =
                "Collision(" +
                    "varn=${varnbit.baseVar.internalId}:${varnbit.baseVar.internalName}, " +
                    "varnbit=${varnbit.internalId}:${varnbit.internalName}, " +
                    "bits=${varnbit.lsb}..${varnbit.msb}" +
                    ")"
        }
    }
}
