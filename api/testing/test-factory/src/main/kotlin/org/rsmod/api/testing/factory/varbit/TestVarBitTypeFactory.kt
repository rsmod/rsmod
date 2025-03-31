package org.rsmod.api.testing.factory.varbit

import org.rsmod.game.type.varbit.UnpackedVarBitType
import org.rsmod.game.type.varbit.VarBitTypeBuilder

public class TestVarBitTypeFactory {
    public fun create(id: Int = 0, init: VarBitTypeBuilder.() -> Unit = {}): UnpackedVarBitType =
        VarBitTypeBuilder()
            .apply {
                internal = "test_varbit_type"
                baseVar = 0
                lsb = 0
                msb = 0
            }
            .apply(init)
            .build(id)
}
