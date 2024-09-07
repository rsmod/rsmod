package org.rsmod.api.cache.types.npc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.cache.types.testBuf
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.game.entity.npc.NpcPatrolWaypoint
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.util.ParamMap
import org.rsmod.map.CoordGrid

class NpcTypeCodecTest {
    @Test
    fun `encode and decode js5`() {
        val type = createJs5NpcType()
        val encoded = testBuf().encodeConfig { NpcTypeEncoder.encodeJs5(type, this) }
        val decoded = NpcTypeDecoder.decode(encoded).build(type.id)
        assertEquals(type, decoded)
    }

    @Test
    fun `encode and decode full`() {
        val type = createGameNpcType()
        val encoded = testBuf().apply { NpcTypeEncoder.encodeFull(type, this) }
        val decoded = NpcTypeDecoder.decode(encoded).build(type.id)
        assertEquals(type, decoded)
    }

    private fun createJs5NpcType(): UnpackedNpcType =
        UnpackedNpcType(
            internalName = TextUtil.NULL,
            internalId = 239,
            name = "King Black Dragon",
            desc = "",
            models = intArrayOf(17414, 17415, 17429, 17422, 17423),
            size = 5,
            readyAnim = 90,
            walkAnim = 4635,
            turnBackAnim = -1,
            turnLeftAnim = -1,
            turnRightAnim = -1,
            category = 347,
            op = arrayOf(null, "Attack", null, null, null),
            recolS = shortArrayOf(),
            recolD = shortArrayOf(),
            retexS = shortArrayOf(),
            retexD = shortArrayOf(),
            head = shortArrayOf(),
            minimap = true,
            vislevel = 276,
            resizeH = 128,
            resizeV = 128,
            alwaysOnTop = false,
            ambient = 0,
            contrast = 0,
            headIconGraphic = intArrayOf(),
            headIconIndex = intArrayOf(),
            turnSpeed = 32,
            multiVarp = -1,
            multiVarBit = -1,
            multiNpcDefault = -1,
            multiNpc = shortArrayOf(),
            active = true,
            rotationFlag = true,
            follower = false,
            lowPriorityOps = false,
            overlayHeight = -1,
            runAnim = -1,
            runTurnBackAnim = -1,
            runTurnLeftAnim = -1,
            runTurnRightAnim = -1,
            crawlAnim = -1,
            crawlTurnBackAnim = -1,
            crawlTurnLeftAnim = -1,
            crawlTurnRightAnim = -1,
            paramMap = ParamMap(mutableMapOf(1361 to 2)),
            moveRestrict = NpcTypeBuilder.DEFAULT_MOVE_RESTRICT,
            defaultMode = NpcTypeBuilder.DEFAULT_MODE,
            blockWalk = NpcTypeBuilder.DEFAULT_BLOCK_WALK,
            respawnRate = 100,
            maxRange = 7,
            wanderRange = 5,
            attackRange = 1,
            huntRange = 5,
            huntMode = -1,
            giveChase = true,
            attack = 240,
            strength = 240,
            defence = 240,
            hitpoints = 240,
            ranged = 1,
            magic = 240,
            timer = -1,
            respawnDir = NpcTypeBuilder.DEFAULT_RESPAWN_DIR,
            patrol = null,
            contentType = NpcTypeBuilder.DEFAULT_CONTENT_TYPE,
        )

    private fun createGameNpcType(): UnpackedNpcType =
        UnpackedNpcType(
            internalName = TextUtil.NULL,
            internalId = 239,
            name = "King Black Dragon",
            desc = "",
            models = intArrayOf(17414, 17415, 17429, 17422, 17423),
            size = 5,
            readyAnim = 90,
            walkAnim = 4635,
            turnBackAnim = -1,
            turnLeftAnim = -1,
            turnRightAnim = -1,
            category = 347,
            op = arrayOf(null, "Attack", null, null, null),
            recolS = shortArrayOf(),
            recolD = shortArrayOf(),
            retexS = shortArrayOf(),
            retexD = shortArrayOf(),
            head = shortArrayOf(),
            minimap = true,
            vislevel = 276,
            resizeH = 128,
            resizeV = 128,
            alwaysOnTop = false,
            ambient = 0,
            contrast = 0,
            headIconGraphic = intArrayOf(),
            headIconIndex = intArrayOf(),
            turnSpeed = 32,
            multiVarp = -1,
            multiVarBit = -1,
            multiNpcDefault = -1,
            multiNpc = shortArrayOf(),
            active = true,
            rotationFlag = true,
            follower = false,
            lowPriorityOps = false,
            overlayHeight = -1,
            runAnim = -1,
            runTurnBackAnim = -1,
            runTurnLeftAnim = -1,
            runTurnRightAnim = -1,
            crawlAnim = -1,
            crawlTurnBackAnim = -1,
            crawlTurnLeftAnim = -1,
            crawlTurnRightAnim = -1,
            paramMap = ParamMap(mutableMapOf(1361 to 2)),
            moveRestrict = MoveRestrict.Normal,
            defaultMode = NpcMode.Wander,
            blockWalk = BlockWalk.Npc,
            respawnRate = 100,
            maxRange = 12,
            wanderRange = 15,
            attackRange = 1,
            huntRange = 5,
            huntMode = -1,
            giveChase = true,
            attack = 240,
            strength = 240,
            defence = 240,
            hitpoints = 240,
            ranged = 1,
            magic = 240,
            timer = -1,
            respawnDir = Direction.East,
            patrol = NpcPatrol(listOf(NpcPatrolWaypoint(CoordGrid(0, 50, 50, 0, 0), 0))),
            contentType = 5,
        )
}
