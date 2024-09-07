@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.varobjbit.VarObjBitBuilder
import org.rsmod.game.type.varobjbit.VarObjBitType

public typealias varobjs = BaseVarObjBits

public object BaseVarObjBits : VarObjBitBuilder() {
    public val charges_16383: VarObjBitType =
        build("charges_16383") {
            startBit = 0
            endBit = 13
        }

    public val snakeboss_blowpipe_darrtype: VarObjBitType =
        build("snakeboss_blowpipe_darttype") {
            startBit = 0
            endBit = 3
        }

    public val snakeboss_blowpipe_dartcount: VarObjBitType =
        build("snakeboss_blowpipe_dartcount") {
            startBit = 4
            endBit = 17
        }

    public val snakeboss_blowpipe_flakes: VarObjBitType =
        build("snakeboss_blowpipe_flakes") {
            startBit = 18
            endBit = 31
        }
}
