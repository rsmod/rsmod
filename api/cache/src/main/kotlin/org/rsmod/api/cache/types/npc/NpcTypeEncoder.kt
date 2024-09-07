package org.rsmod.api.cache.types.npc

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.writeString
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.writeCoordGrid
import org.rsmod.api.cache.util.writeNullableLargeSmart
import org.rsmod.api.cache.util.writeRawParams
import org.rsmod.api.cache.util.writeSmallSmartPlusOne
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType

public object NpcTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedNpcType>,
        serverCache: Boolean,
        reusableBuf: ByteBuf,
    ): List<UnpackedNpcType> {
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.NPC
        val packed = mutableListOf<UnpackedNpcType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                reusableBuf.clear().encodeConfig {
                    encodeJs5(type, this)
                    if (serverCache) {
                        encodeGame(type, this)
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
        }
        return packed
    }

    public fun encodeFull(type: UnpackedNpcType, data: ByteBuf): ByteBuf =
        data.encodeConfig {
            encodeJs5(type, this)
            encodeGame(type, this)
        }

    public fun encodeJs5(type: UnpackedNpcType, data: ByteBuf): Unit =
        with(type) {
            if (models.isNotEmpty()) {
                data.writeByte(1)
                data.writeByte(models.size)
                for (model in models) {
                    data.writeShort(model)
                }
            }

            if (name.isNotEmpty()) {
                data.writeByte(2)
                data.writeString(name)
            }

            if (size != NpcTypeBuilder.DEFAULT_SIZE) {
                data.writeByte(12)
                data.writeByte(size)
            }

            if (readyAnim != NpcTypeBuilder.DEFAULT_ANIM) {
                data.writeByte(13)
                data.writeShort(readyAnim)
            }

            val hasWalkAnim = walkAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasTurnBackAnim = turnBackAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasTurnLeftAnim = turnLeftAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasTurnRightAnim = turnRightAnim != NpcTypeBuilder.DEFAULT_ANIM
            if (hasTurnBackAnim) {
                data.writeByte(17)
                data.writeShort(walkAnim)
                data.writeShort(turnBackAnim)
                data.writeShort(turnLeftAnim)
                data.writeShort(turnRightAnim)
            } else {
                if (hasWalkAnim) {
                    data.writeByte(14)
                    data.writeShort(walkAnim)
                }
                if (hasTurnLeftAnim) {
                    data.writeByte(15)
                    data.writeShort(turnLeftAnim)
                }
                if (hasTurnRightAnim) {
                    data.writeByte(16)
                    data.writeShort(turnRightAnim)
                }
            }

            if (category != NpcTypeBuilder.DEFAULT_CATEGORY) {
                data.writeByte(18)
                data.writeShort(category)
            }

            for (i in op.indices) {
                val op = op[i] ?: continue
                data.writeByte(30 + i)
                data.writeString(op)
            }

            if (recolS.isNotEmpty()) {
                check(recolS.size == recolD.size)
                data.writeByte(40)
                data.writeByte(recolS.size)
                for (i in recolS.indices) {
                    data.writeShort(recolS[i].toInt())
                    data.writeShort(recolD[i].toInt())
                }
            }

            if (retexS.isNotEmpty()) {
                check(retexS.size == retexD.size)
                data.writeByte(41)
                data.writeByte(retexS.size)
                for (i in retexS.indices) {
                    data.writeShort(retexS[i].toInt())
                    data.writeShort(retexD[i].toInt())
                }
            }

            if (head.isNotEmpty()) {
                data.writeByte(60)
                data.writeByte(head.size)
                for (i in head.indices) {
                    data.writeShort(head[i].toInt())
                }
            }

            if (attack != NpcTypeBuilder.DEFAULT_STAT_LEVEL) {
                data.writeByte(74)
                data.writeShort(attack)
            }

            if (defence != NpcTypeBuilder.DEFAULT_STAT_LEVEL) {
                data.writeByte(75)
                data.writeShort(defence)
            }

            if (strength != NpcTypeBuilder.DEFAULT_STAT_LEVEL) {
                data.writeByte(76)
                data.writeShort(strength)
            }

            if (hitpoints != NpcTypeBuilder.DEFAULT_STAT_LEVEL) {
                data.writeByte(77)
                data.writeShort(hitpoints)
            }

            if (ranged != NpcTypeBuilder.DEFAULT_STAT_LEVEL) {
                data.writeByte(78)
                data.writeShort(ranged)
            }

            if (magic != NpcTypeBuilder.DEFAULT_STAT_LEVEL) {
                data.writeByte(79)
                data.writeShort(magic)
            }

            if (!minimap) {
                data.writeByte(93)
            }

            if (vislevel != NpcTypeBuilder.DEFAULT_VISLEVEL) {
                data.writeByte(95)
                data.writeShort(vislevel)
            }

            if (resizeH != NpcTypeBuilder.DEFAULT_RESIZE_H) {
                data.writeByte(97)
                data.writeShort(resizeH)
            }

            if (resizeV != NpcTypeBuilder.DEFAULT_RESIZE_V) {
                data.writeByte(98)
                data.writeShort(resizeV)
            }

            if (alwaysOnTop) {
                data.writeByte(99)
            }

            if (ambient != 0) {
                data.writeByte(100)
                data.writeByte(ambient)
            }

            if (contrast != 0) {
                data.writeByte(101)
                data.writeByte(contrast / 5)
            }

            if (headIconGraphic.isNotEmpty()) {
                val enabledBits = headIconGraphic.fold(0) { sum, _ -> sum or 1 }
                data.writeByte(102)
                data.writeByte(enabledBits)
                for (i in headIconGraphic.indices) {
                    if ((enabledBits and (1 shl i)) != 0) {
                        data.writeNullableLargeSmart(headIconGraphic[i])
                        data.writeSmallSmartPlusOne(headIconIndex[i])
                    }
                }
            }

            if (turnSpeed != NpcTypeBuilder.DEFAULT_TURN_SPEED) {
                data.writeByte(103)
                data.writeShort(turnSpeed)
            }

            if (multiNpc.isNotEmpty()) {
                val hasDefault = multiNpcDefault != NpcTypeBuilder.DEFAULT_MULTI_DEFAULT
                if (hasDefault) {
                    data.writeByte(118)
                } else {
                    data.writeByte(106)
                }
                data.writeShort(multiVarBit)
                data.writeShort(multiVarp)
                if (hasDefault) {
                    data.writeShort(multiNpcDefault)
                }
                data.writeByte(multiNpc.size - 1)
                for (i in multiNpc.indices) {
                    data.writeShort(multiNpc[i].toInt())
                }
            }

            if (!active) {
                data.writeByte(107)
            }

            if (!rotationFlag) {
                data.writeByte(109)
            }

            val hasRunAnim = runAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasRunTurnBackAnim = runTurnBackAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasRunTurnLeftAnim = runTurnLeftAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasRunTurnRightAnim = runTurnRightAnim != NpcTypeBuilder.DEFAULT_ANIM
            if (hasRunAnim && !hasRunTurnBackAnim && !hasRunTurnLeftAnim && !hasRunTurnRightAnim) {
                data.writeByte(114)
                data.writeShort(runAnim)
            } else if (hasRunAnim) {
                data.writeByte(115)
                data.writeShort(runAnim)
                data.writeShort(runTurnBackAnim)
                data.writeShort(runTurnLeftAnim)
                data.writeShort(runTurnRightAnim)
            }

            val hasCrawlAnim = crawlAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasCrawlTurnBackAnim = crawlTurnBackAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasCrawlTurnLeftAnim = crawlTurnLeftAnim != NpcTypeBuilder.DEFAULT_ANIM
            val hasCrawlTurnRightAnim = crawlTurnRightAnim != NpcTypeBuilder.DEFAULT_ANIM
            if (
                hasCrawlAnim &&
                    !hasCrawlTurnBackAnim &&
                    !hasCrawlTurnLeftAnim &&
                    !hasCrawlTurnRightAnim
            ) {
                data.writeByte(116)
                data.writeShort(crawlAnim)
            } else if (hasCrawlAnim) {
                data.writeByte(117)
                data.writeShort(crawlAnim)
                data.writeShort(crawlTurnBackAnim)
                data.writeShort(crawlTurnLeftAnim)
                data.writeShort(crawlTurnRightAnim)
            }

            if (follower) {
                data.writeByte(122)
            }

            if (lowPriorityOps) {
                data.writeByte(123)
            }

            if (overlayHeight != NpcTypeBuilder.DEFAULT_OVERLAY_HEIGHT) {
                data.writeByte(124)
                data.writeShort(overlayHeight)
            }

            val params = paramMap?.primitiveMap
            if (params?.isNotEmpty() == true) {
                data.writeByte(249)
                data.writeRawParams(params)
            }
        }

    public fun encodeGame(type: UnpackedNpcType, data: ByteBuf): Unit =
        with(type) {
            if (desc.isNotBlank()) {
                data.writeByte(3)
                data.writeString(desc)
            }

            if (moveRestrict != NpcTypeBuilder.DEFAULT_MOVE_RESTRICT) {
                data.writeByte(200)
                data.writeByte(moveRestrict.id)
            }

            if (defaultMode != NpcTypeBuilder.DEFAULT_MODE) {
                data.writeByte(201)
                data.writeByte(defaultMode.id)
            }

            if (blockWalk != NpcTypeBuilder.DEFAULT_BLOCK_WALK) {
                data.writeByte(202)
                data.writeByte(blockWalk.id)
            }

            val patrol = patrol
            if (patrol?.isNotEmpty() == true) {
                data.writeByte(203)
                data.writeByte(patrol.size - 1)
                for (i in patrol.indices) {
                    data.writeCoordGrid(patrol[i].destination)
                    data.writeByte(patrol[i].pauseDelay)
                }
            }

            if (respawnRate != NpcTypeBuilder.DEFAULT_RESPAWN_RATE) {
                check(respawnRate < 256) { "`respawnRate` cannot be over 255." }
                data.writeByte(204)
                data.writeByte(respawnRate)
            }

            if (maxRange != NpcTypeBuilder.DEFAULT_MAX_RANGE) {
                data.writeByte(205)
                data.writeByte(maxRange)
            }

            if (wanderRange != NpcTypeBuilder.DEFAULT_WANDER_RANGE) {
                data.writeByte(206)
                data.writeByte(wanderRange)
            }

            if (attackRange != NpcTypeBuilder.DEFAULT_ATTACK_RANGE) {
                data.writeByte(207)
                data.writeByte(attackRange)
            }

            if (huntRange != NpcTypeBuilder.DEFAULT_HUNT_RANGE) {
                data.writeByte(208)
                data.writeByte(huntRange)
            }

            if (huntMode != NpcTypeBuilder.DEFAULT_HUNT_MODE) {
                data.writeByte(209)
                data.writeByte(huntMode)
            }

            @Suppress("KotlinConstantConditions")
            if (giveChase != NpcTypeBuilder.DEFAULT_GIVE_CHASE) {
                data.writeByte(210)
            }

            if (timer != NpcTypeBuilder.DEFAULT_TIMER) {
                data.writeByte(211)
                data.writeShort(timer)
            }

            if (respawnDir != NpcTypeBuilder.DEFAULT_RESPAWN_DIR) {
                data.writeByte(212)
                data.writeByte(respawnDir.id)
            }

            if (contentType != NpcTypeBuilder.DEFAULT_CONTENT_TYPE) {
                data.writeByte(213)
                data.writeShort(contentType)
            }
        }
}
