@file:Suppress("SpellCheckingInspection")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.bas.BasBuilder
import org.rsmod.game.type.bas.BasType

public typealias baseanimsets = BaseBas

public object BaseBas : BasBuilder() {
    public val human_default: BasType =
        build("human_default") {
            readyAnim = seqs.human_ready
            turnAnim = seqs.human_turnonspot
            walkAnim = seqs.human_walk_f
            walkAnimBack = seqs.human_walk_b
            walkAnimLeft = seqs.human_walk_l
            walkAnimRight = seqs.human_walk_r
            runAnim = seqs.human_running
        }
}
