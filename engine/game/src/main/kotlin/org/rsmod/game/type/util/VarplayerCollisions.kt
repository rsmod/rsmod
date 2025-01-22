package org.rsmod.game.type.util

import java.util.BitSet
import org.rsmod.game.type.varbit.UnpackedVarBitType
import org.rsmod.game.type.varp.UnpackedVarpType

public object VarplayerCollisions {
    public fun detect(
        varps: Iterable<UnpackedVarpType>,
        varbits: Iterable<UnpackedVarBitType>,
    ): List<Error> {
        val results = mutableListOf<Error>()

        val varpsBitSets = varps.associate { it.id to BitSet() }
        for (varbit in varbits) {
            val varpBitSet = varpsBitSets[varbit.baseVar.id]
            if (varpBitSet == null) {
                results += Error.InvalidBaseVar(varbit)
                continue
            }

            val varbitRange = varbit.asBitSet()
            if (varpBitSet.intersects(varbitRange)) {
                results += Error.VarpBitCollision(varbit)
                continue
            }

            varpBitSet.or(varbitRange)
        }

        return results
    }

    private fun UnpackedVarBitType.asBitSet(): BitSet = BitSet().apply { set(lsb, msb) }

    public sealed class Error(public val varbit: UnpackedVarBitType) {
        public class InvalidBaseVar(varbit: UnpackedVarBitType) : Error(varbit) {
            override fun toString(): String =
                "InvalidVarp(varp=${varbit.baseVar.internalId}, varbit=${varbit.internalId})"
        }

        public class VarpBitCollision(varbit: UnpackedVarBitType) : Error(varbit) {
            override fun toString(): String =
                "Collision(" +
                    "varp=${varbit.baseVar.internalId}:${varbit.baseVar.internalName}, " +
                    "varbit=${varbit.internalId}:${varbit.internalName}, " +
                    "bits=${varbit.lsb}..${varbit.msb}" +
                    ")"
        }
    }
}
