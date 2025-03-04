package org.rsmod.game.entity.npc

import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hitmark

public interface NpcInfoProtocol {
    public fun setSequence(seq: Int, delay: Int)

    public fun setSpotanim(spotanim: Int, delay: Int, height: Int, slot: Int)

    public fun setSay(text: String)

    public fun setFacePathingEntity(slot: Int)

    public fun setFaceSquare(x: Int, z: Int, instant: Boolean)

    public fun setTransmog(type: Int)

    public fun resetTransmog(originalType: Int)

    public fun showHeadbar(headbar: Headbar)

    public fun showHitmark(hitmark: Hitmark)

    public fun walk(deltaX: Int, deltaZ: Int)

    public fun teleport(x: Int, z: Int, level: Int, jump: Boolean)

    public fun disable()

    public fun hide()

    public fun reveal()
}

public data object NoopNpcInfo : NpcInfoProtocol {
    override fun setSequence(seq: Int, delay: Int) {}

    override fun setSpotanim(spotanim: Int, delay: Int, height: Int, slot: Int) {}

    override fun setSay(text: String) {}

    override fun showHeadbar(headbar: Headbar) {}

    override fun showHitmark(hitmark: Hitmark) {}

    override fun setFacePathingEntity(slot: Int) {}

    override fun setFaceSquare(x: Int, z: Int, instant: Boolean) {}

    override fun setTransmog(type: Int) {}

    override fun resetTransmog(originalType: Int) {}

    override fun walk(deltaX: Int, deltaZ: Int) {}

    override fun teleport(x: Int, z: Int, level: Int, jump: Boolean) {}

    override fun disable() {}

    override fun hide() {}

    override fun reveal() {}
}
