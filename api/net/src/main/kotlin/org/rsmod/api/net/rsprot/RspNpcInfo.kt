package org.rsmod.api.net.rsprot

import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcAvatar
import org.rsmod.game.entity.npc.NpcInfoProtocol
import org.rsmod.game.entity.npc.OpVisibility
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hitmark

class RspNpcInfo(val rspAvatar: NpcAvatar) : NpcInfoProtocol {
    override fun setSequence(seq: Int, delay: Int) {
        rspAvatar.extendedInfo.setSequence(seq, delay)
    }

    override fun setSpotanim(spotanim: Int, delay: Int, height: Int, slot: Int) {
        rspAvatar.extendedInfo.setSpotAnim(slot, spotanim, delay, height)
    }

    override fun setSay(text: String) {
        rspAvatar.extendedInfo.setSay(text)
    }

    override fun setFacePathingEntity(slot: Int) {
        rspAvatar.extendedInfo.setFacePathingEntity(slot)
    }

    override fun setFaceSquare(x: Int, z: Int, instant: Boolean) {
        rspAvatar.extendedInfo.setFaceCoord(x, z, instant)
    }

    override fun setTransmog(type: Int) {
        rspAvatar.setId(type)
        rspAvatar.extendedInfo.setTransmogrification(type)
    }

    override fun resetTransmog(originalType: Int) {
        rspAvatar.setId(originalType)
        rspAvatar.extendedInfo.setTransmogrification(originalType)
    }

    override fun showHeadbar(headbar: Headbar) {
        rspAvatar.extendedInfo.addHeadBar(
            sourceIndex = if (headbar.isNoSource) -1 else headbar.sourceSlot,
            sourceType = headbar.self,
            otherType = if (headbar.isPrivate) -1 else headbar.public,
            startFill = headbar.startFill,
            endFill = headbar.endFill,
            startTime = headbar.startTime,
            endTime = headbar.endTime,
        )
    }

    override fun showHitmark(hitmark: Hitmark) {
        rspAvatar.extendedInfo.addHitMark(
            sourceIndex = if (hitmark.isNoSource) -1 else hitmark.sourceSlot,
            sourceType = hitmark.source,
            otherType = if (hitmark.isPrivate) -1 else hitmark.public,
            value = hitmark.damage,
            delay = hitmark.delay,
        )
    }

    override fun toggleOps(ops: OpVisibility) {
        rspAvatar.extendedInfo.setVisibleOps(ops.packed.toByte())
    }

    override fun setHeadIcon(slot: Int, graphic: Int, index: Int) {
        rspAvatar.extendedInfo.setHeadIconChange(slot, graphic, index)
    }

    override fun exactMove(
        deltaX1: Int,
        deltaZ1: Int,
        deltaX2: Int,
        deltaZ2: Int,
        delay1: Int,
        delay2: Int,
        direction: Int,
    ) {
        rspAvatar.extendedInfo.setExactMove(
            deltaX1 = deltaX1,
            deltaZ1 = deltaZ1,
            delay1 = delay1,
            deltaX2 = deltaX2,
            deltaZ2 = deltaZ2,
            delay2 = delay2,
            angle = direction,
        )
    }

    override fun crawl(deltaX: Int, deltaZ: Int) {
        rspAvatar.crawl(deltaX, deltaZ)
    }

    override fun walk(deltaX: Int, deltaZ: Int) {
        rspAvatar.walk(deltaX, deltaZ)
    }

    override fun run(deltaX1: Int, deltaZ1: Int, deltaX2: Int, deltaZ2: Int) {
        rspAvatar.walk(deltaX1, deltaZ1)
        rspAvatar.walk(deltaX2, deltaZ2)
    }

    override fun teleport(x: Int, z: Int, level: Int, jump: Boolean) {
        rspAvatar.teleport(level, x, z, jump)
    }

    override fun disable() {
        rspAvatar.setInaccessible(true)
    }

    override fun hide() {
        rspAvatar.setInaccessible(true)
    }

    override fun reveal() {
        rspAvatar.setInaccessible(false)
    }

    override fun isActive(): Boolean {
        return rspAvatar.isActive()
    }
}
