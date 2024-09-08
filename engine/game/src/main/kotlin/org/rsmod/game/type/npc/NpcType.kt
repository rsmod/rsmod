package org.rsmod.game.type.npc

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.util.ParamMap

public sealed class NpcType(internal var internalId: Int?, internal var internalName: String?) {
    public val id: Int
        get() = internalId ?: error("`internalId` must be set.")

    public val internalNameGet: String?
        get() = internalName
}

public class HashedNpcType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
) : NpcType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "NpcType(internalName='$internalName', internalId=$internalId, supposedHash=$supposedHash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedNpcType) return false

        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public class UnpackedNpcType(
    public val name: String,
    public val desc: String,
    public val models: IntArray,
    public val size: Int,
    public val readyAnim: Int,
    public val walkAnim: Int,
    public val turnBackAnim: Int,
    public val turnLeftAnim: Int,
    public val turnRightAnim: Int,
    public val category: Int,
    public val op: Array<String?>,
    public val recolS: ShortArray,
    public val recolD: ShortArray,
    public val retexS: ShortArray,
    public val retexD: ShortArray,
    public val head: ShortArray,
    public val minimap: Boolean,
    public val vislevel: Int,
    public val resizeH: Int,
    public val resizeV: Int,
    public val alwaysOnTop: Boolean,
    public val ambient: Int,
    public val contrast: Int,
    public val headIconGraphic: IntArray,
    public val headIconIndex: IntArray,
    public val turnSpeed: Int,
    public val multiVarp: Int,
    public val multiVarBit: Int,
    public val multiNpcDefault: Int,
    public val multiNpc: ShortArray,
    public val active: Boolean,
    public val rotationFlag: Boolean,
    public val follower: Boolean,
    public val lowPriorityOps: Boolean,
    public val overlayHeight: Int,
    public val runAnim: Int,
    public val runTurnBackAnim: Int,
    public val runTurnLeftAnim: Int,
    public val runTurnRightAnim: Int,
    public val crawlAnim: Int,
    public val crawlTurnBackAnim: Int,
    public val crawlTurnLeftAnim: Int,
    public val crawlTurnRightAnim: Int,
    public val paramMap: ParamMap?,
    public val moveRestrict: MoveRestrict,
    public val defaultMode: NpcMode,
    public val blockWalk: BlockWalk,
    public val respawnRate: Int,
    public val maxRange: Int,
    public val wanderRange: Int,
    public val attackRange: Int,
    public val huntRange: Int,
    public val huntMode: Int,
    public val giveChase: Boolean,
    public val attack: Int,
    public val strength: Int,
    public val defence: Int,
    public val hitpoints: Int,
    public val ranged: Int,
    public val magic: Int,
    public val timer: Int,
    public val respawnDir: Direction,
    public val patrol: NpcPatrol?,
    public val contentType: Int,
    internalId: Int,
    internalName: String,
) : NpcType(internalId, internalName) {
    public fun <T : Any> param(type: ParamType<T>): T {
        val params = paramMap
        if (params == null) {
            return type.typedDefault
                ?: error("Param `$type` does not have a default value. Use `paramOrNull` instead.")
        }
        val value = params[type]
        if (value != null) {
            return value
        }
        return type.typedDefault ?: error("NpcType does not have no-default param `$type` defined.")
    }

    public fun <T : Any> paramOrNull(type: ParamType<T>): T? = paramMap?.get(type)

    public fun toHashedType(): HashedNpcType =
        HashedNpcType(
            internalId = internalId,
            internalName = internalName,
            startHash = computeIdentityHash(),
        )

    public fun computeIdentityHash(): Long {
        var result = name.hashCode().toLong()
        result = 61 * result + size
        result = 61 * result + category
        result = 61 * result + op.contentHashCode()
        result = 61 * result + minimap.hashCode()
        result = 61 * result + vislevel
        result = 61 * result + alwaysOnTop.hashCode()
        result = 61 * result + headIconGraphic.contentHashCode()
        result = 61 * result + headIconIndex.contentHashCode()
        result = 61 * result + multiVarp
        result = 61 * result + multiVarBit
        result = 61 * result + multiNpcDefault
        result = 61 * result + multiNpc.contentHashCode()
        result = 61 * result + active.hashCode()
        result = 61 * result + follower.hashCode()
        result = 61 * result + lowPriorityOps.hashCode()
        result = 61 * result + (paramMap?.hashCode() ?: 0)
        result = 61 * result + moveRestrict.id
        result = 61 * result + defaultMode.id
        result = 61 * result + blockWalk.id
        result = 61 * result + huntMode
        result = 61 * result + giveChase.hashCode()
        result = 61 * result + (patrol?.hashCode() ?: 0)
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedNpcType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "internalHash=${computeIdentityHash()}, " +
            "contentType=$contentType, " +
            "name='$name', " +
            "desc='$desc', " +
            "models=${models.contentToString()}, " +
            "size=$size, " +
            "readyAnim=$readyAnim, " +
            "walkAnim=$walkAnim, " +
            "turnBackAnim=$turnBackAnim, " +
            "turnLeftAnim=$turnLeftAnim, " +
            "turnRightAnim=$turnRightAnim, " +
            "category=$category, " +
            "op=${op.contentToString()}, " +
            "recolS=${recolS.contentToString()}, " +
            "recolD=${recolD.contentToString()}, " +
            "retexS=${retexS.contentToString()}, " +
            "retexD=${retexD.contentToString()}, " +
            "head=${head.contentToString()}, " +
            "minimap=$minimap, " +
            "vislevel=$vislevel, " +
            "resizeH=$resizeH, " +
            "resizeV=$resizeV, " +
            "alwaysOnTop=$alwaysOnTop, " +
            "ambient=$ambient, " +
            "contrast=$contrast, " +
            "headIconGraphic=${headIconGraphic.contentToString()}, " +
            "headIconIndex=${headIconIndex.contentToString()}, " +
            "turnSpeed=$turnSpeed, " +
            "multiVarp=$multiVarp, " +
            "multiVarBit=$multiVarBit, " +
            "multiNpcDefault=$multiVarBit, " +
            "multiNpc=${multiNpc.contentToString()}, " +
            "active=$active, " +
            "rotationFlag=$rotationFlag, " +
            "follower=$follower, " +
            "lowPriorityOps=$lowPriorityOps, " +
            "overlayHeight=$overlayHeight, " +
            "runAnim=$runAnim, " +
            "runTurnBackAnim=$runTurnBackAnim, " +
            "runTurnLeftAnim=$runTurnLeftAnim, " +
            "runTurnRightAnim=$runTurnRightAnim, " +
            "crawlAnim=$crawlAnim, " +
            "crawlTurnBackAnim=$crawlTurnBackAnim, " +
            "crawlTurnLeftAnim=$crawlTurnLeftAnim, " +
            "crawlTurnRightAnim=$crawlTurnRightAnim, " +
            "params=$paramMap, " +
            "moveRestrict=$moveRestrict, " +
            "defaultMode=$defaultMode, " +
            "blockWalk=$blockWalk, " +
            "respawnRate=$respawnRate, " +
            "maxRange=$maxRange, " +
            "wanderRange=$wanderRange, " +
            "attackRange=$attackRange, " +
            "huntRange=$huntRange, " +
            "huntMode=$huntMode, " +
            "giveChase=$giveChase, " +
            "attack=$attack, " +
            "strength=$strength, " +
            "defence=$defence, " +
            "hitpoints=$hitpoints, " +
            "ranged=$ranged, " +
            "magic=$magic, " +
            "timer=$timer, " +
            "respawnDir=$respawnDir, " +
            "patrol=${patrol?.hashCode()}" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedNpcType) return false

        if (name != other.name) return false
        if (desc != other.desc) return false
        if (!models.contentEquals(other.models)) return false
        if (size != other.size) return false
        if (readyAnim != other.readyAnim) return false
        if (walkAnim != other.walkAnim) return false
        if (turnBackAnim != other.turnBackAnim) return false
        if (turnLeftAnim != other.turnLeftAnim) return false
        if (turnRightAnim != other.turnRightAnim) return false
        if (category != other.category) return false
        if (!op.contentEquals(other.op)) return false
        if (!recolS.contentEquals(other.recolS)) return false
        if (!recolD.contentEquals(other.recolD)) return false
        if (!retexS.contentEquals(other.retexS)) return false
        if (!retexD.contentEquals(other.retexD)) return false
        if (!head.contentEquals(other.head)) return false
        if (minimap != other.minimap) return false
        if (vislevel != other.vislevel) return false
        if (resizeH != other.resizeH) return false
        if (resizeV != other.resizeV) return false
        if (alwaysOnTop != other.alwaysOnTop) return false
        if (ambient != other.ambient) return false
        if (contrast != other.contrast) return false
        if (!headIconGraphic.contentEquals(other.headIconGraphic)) return false
        if (!headIconIndex.contentEquals(other.headIconIndex)) return false
        if (turnSpeed != other.turnSpeed) return false
        if (multiVarp != other.multiVarp) return false
        if (multiVarBit != other.multiVarBit) return false
        if (multiNpcDefault != other.multiNpcDefault) return false
        if (!multiNpc.contentEquals(other.multiNpc)) return false
        if (active != other.active) return false
        if (rotationFlag != other.rotationFlag) return false
        if (follower != other.follower) return false
        if (lowPriorityOps != other.lowPriorityOps) return false
        if (overlayHeight != other.overlayHeight) return false
        if (runAnim != other.runAnim) return false
        if (runTurnBackAnim != other.runTurnBackAnim) return false
        if (runTurnLeftAnim != other.runTurnLeftAnim) return false
        if (runTurnRightAnim != other.runTurnRightAnim) return false
        if (crawlAnim != other.crawlAnim) return false
        if (crawlTurnBackAnim != other.crawlTurnBackAnim) return false
        if (crawlTurnLeftAnim != other.crawlTurnLeftAnim) return false
        if (crawlTurnRightAnim != other.crawlTurnRightAnim) return false
        if (paramMap != other.paramMap) return false
        if (moveRestrict != other.moveRestrict) return false
        if (defaultMode != other.defaultMode) return false
        if (blockWalk != other.blockWalk) return false
        if (respawnRate != other.respawnRate) return false
        if (maxRange != other.maxRange) return false
        if (wanderRange != other.wanderRange) return false
        if (attackRange != other.attackRange) return false
        if (huntRange != other.huntRange) return false
        if (huntMode != other.huntMode) return false
        if (giveChase != other.giveChase) return false
        if (attack != other.attack) return false
        if (strength != other.strength) return false
        if (defence != other.defence) return false
        if (hitpoints != other.hitpoints) return false
        if (ranged != other.ranged) return false
        if (magic != other.magic) return false
        if (timer != other.timer) return false
        if (respawnDir != other.respawnDir) return false
        if (patrol != null) {
            if (other.patrol == null) return false
            if (patrol != other.patrol) return false
        } else if (other.patrol != null) return false
        if (contentType != other.contentType) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + models.contentHashCode()
        result = 31 * result + size
        result = 31 * result + readyAnim
        result = 31 * result + walkAnim
        result = 31 * result + turnBackAnim
        result = 31 * result + turnLeftAnim
        result = 31 * result + turnRightAnim
        result = 31 * result + category
        result = 31 * result + op.contentHashCode()
        result = 31 * result + recolS.contentHashCode()
        result = 31 * result + recolD.contentHashCode()
        result = 31 * result + retexS.contentHashCode()
        result = 31 * result + retexD.contentHashCode()
        result = 31 * result + head.contentHashCode()
        result = 31 * result + minimap.hashCode()
        result = 31 * result + vislevel
        result = 31 * result + resizeH
        result = 31 * result + resizeV
        result = 31 * result + alwaysOnTop.hashCode()
        result = 31 * result + ambient
        result = 31 * result + contrast
        result = 31 * result + headIconGraphic.contentHashCode()
        result = 31 * result + headIconIndex.contentHashCode()
        result = 31 * result + turnSpeed
        result = 31 * result + multiVarp
        result = 31 * result + multiVarBit
        result = 31 * result + multiNpcDefault
        result = 31 * result + multiNpc.contentHashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + rotationFlag.hashCode()
        result = 31 * result + follower.hashCode()
        result = 31 * result + lowPriorityOps.hashCode()
        result = 31 * result + overlayHeight
        result = 31 * result + runAnim
        result = 31 * result + runTurnBackAnim
        result = 31 * result + runTurnLeftAnim
        result = 31 * result + runTurnRightAnim
        result = 31 * result + crawlAnim
        result = 31 * result + crawlTurnBackAnim
        result = 31 * result + crawlTurnLeftAnim
        result = 31 * result + crawlTurnRightAnim
        result = 31 * result + (paramMap?.hashCode() ?: 0)
        result = 31 * result + moveRestrict.hashCode()
        result = 31 * result + defaultMode.hashCode()
        result = 31 * result + blockWalk.hashCode()
        result = 31 * result + respawnRate
        result = 31 * result + maxRange
        result = 31 * result + wanderRange
        result = 31 * result + attackRange
        result = 31 * result + huntRange
        result = 31 * result + huntMode
        result = 31 * result + giveChase.hashCode()
        result = 31 * result + attack
        result = 31 * result + strength
        result = 31 * result + defence
        result = 31 * result + hitpoints
        result = 31 * result + ranged
        result = 31 * result + magic
        result = 31 * result + timer
        result = 31 * result + respawnDir.hashCode()
        result = 31 * result + (patrol?.hashCode() ?: 0)
        result = 31 * result + contentType
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
