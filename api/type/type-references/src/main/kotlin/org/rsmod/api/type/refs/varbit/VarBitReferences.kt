package org.rsmod.api.type.refs.varbit

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.varbit.HashedVarBitType
import org.rsmod.game.type.varbit.VarBitType

public abstract class VarBitReferences : HashTypeReferences<VarBitType>(VarBitType::class.java) {
    override fun find(hash: Long): VarBitType {
        val type = HashedVarBitType(hash)
        cache += type
        return type
    }
}
