package org.rsmod.game.type.bas

import org.rsmod.game.type.seq.SeqType

@DslMarker private annotation class BasBuilderDsl

@BasBuilderDsl
public class BasTypeBuilder(public var internal: String? = null) {
    public var readyAnim: SeqType? = null
    public var turnAnim: SeqType? = null
    public var walkAnim: SeqType? = null
    public var walkAnimBack: SeqType? = null
    public var walkAnimLeft: SeqType? = null
    public var walkAnimRight: SeqType? = null
    public var runAnim: SeqType? = null

    public fun build(id: Int): UnpackedBasType {
        val internal = checkNotNull(internal) { "`internal` must be set. (id=$id)" }
        val readyAnim = checkNotNull(readyAnim) { "`readyAnim` must be set. (id=$id)" }
        val turnAnim = checkNotNull(turnAnim) { "`turnAnim` must be set. (id=$id)" }
        val walkAnim = checkNotNull(walkAnim) { "`walkAnim` must be set. (id=$id)" }
        val walkAnimBack = checkNotNull(walkAnimBack) { "`walkAnimBack` must be set. (id=$id)" }
        val walkAnimLeft = checkNotNull(walkAnimLeft) { "`walkAnimLeft` must be set. (id=$id)" }
        val walkAnimRight = checkNotNull(walkAnimRight) { "`walkAnimRight` must be set. (id=$id)" }
        val runAnim = checkNotNull(runAnim) { "`runAnim` must be set. (id=$id)" }
        return UnpackedBasType(
            readyAnim = readyAnim,
            turnOnSpot = turnAnim,
            walkForward = walkAnim,
            walkBack = walkAnimBack,
            walkLeft = walkAnimLeft,
            walkRight = walkAnimRight,
            running = runAnim,
            internalId = id,
            internalName = internal,
        )
    }
}
