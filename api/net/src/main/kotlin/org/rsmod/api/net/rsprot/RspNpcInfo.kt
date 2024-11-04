package org.rsmod.api.net.rsprot

import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcAvatar
import org.rsmod.game.entity.npc.NpcInfoProtocol

class RspNpcInfo(val rspAvatar: NpcAvatar) : NpcInfoProtocol {
    override fun setSequence(seq: Int, delay: Int) {
        rspAvatar.extendedInfo.setSequence(seq, delay)
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
        rspAvatar.extendedInfo.setTransmogrification(type)
    }

    override fun walk(deltaX: Int, deltaZ: Int) {
        rspAvatar.walk(deltaX, deltaZ)
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
}
