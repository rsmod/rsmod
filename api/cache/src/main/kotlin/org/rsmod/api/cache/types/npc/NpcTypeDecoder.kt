package org.rsmod.api.cache.types.npc

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.readString
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readCoordGrid
import org.rsmod.api.cache.util.readNullableLargeSmart
import org.rsmod.api.cache.util.readRawParams
import org.rsmod.api.cache.util.readUnsignedShortOrNull
import org.rsmod.api.cache.util.readUnsignedSmallSmartPlusOne
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcPatrol
import org.rsmod.game.entity.npc.NpcPatrolWaypoint
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.NpcTypeList
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.util.CompactableIntArray
import org.rsmod.game.type.util.ParamMap

public object NpcTypeDecoder {
    public fun decodeAll(cache: Cache): NpcTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedNpcType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.NPC)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.NPC, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return NpcTypeList(types)
    }

    public fun decode(data: ByteBuf): NpcTypeBuilder {
        val builder = NpcTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: NpcTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    val count = data.readUnsignedByte().toInt()
                    val models = IntArray(count)
                    repeat(count) { models[it] = data.readUnsignedShort() }
                    this.models = CompactableIntArray(models)
                }
                2 -> name = data.readString()
                3 -> desc = data.readString()
                12 -> size = data.readUnsignedByte().toInt()
                13 -> readyAnim = data.readUnsignedShort()
                14 -> walkAnim = data.readUnsignedShort()
                15 -> turnLeftAnim = data.readUnsignedShort()
                16 -> turnRightAnim = data.readUnsignedShort()
                17 -> {
                    walkAnim = data.readUnsignedShort()
                    turnBackAnim = data.readUnsignedShort()
                    turnLeftAnim = data.readUnsignedShort()
                    turnRightAnim = data.readUnsignedShort()
                }
                18 -> category = data.readUnsignedShort()
                in 30 until 35 -> {
                    op[code - 30] = data.readString()
                }
                40,
                41 -> {
                    val count = data.readUnsignedByte().toInt()
                    val src = IntArray(count)
                    val dest = IntArray(count)
                    repeat(count) {
                        src[it] = data.readUnsignedShort()
                        dest[it] = data.readUnsignedShort()
                    }
                    when (code) {
                        40 -> {
                            recolS = CompactableIntArray(src)
                            recolD = CompactableIntArray(dest)
                        }
                        41 -> {
                            retexS = CompactableIntArray(src)
                            retexD = CompactableIntArray(dest)
                        }
                        else -> throw NotImplementedError("Unhandled .npc config code: $code")
                    }
                }
                60 -> {
                    val count = data.readUnsignedByte().toInt()
                    val models = IntArray(count)
                    repeat(count) { models[it] = data.readUnsignedShort() }
                    this.head = CompactableIntArray(models)
                }
                74 -> attack = data.readUnsignedShort()
                75 -> defence = data.readUnsignedShort()
                76 -> strength = data.readUnsignedShort()
                77 -> hitpoints = data.readUnsignedShort()
                78 -> ranged = data.readUnsignedShort()
                79 -> magic = data.readUnsignedShort()
                93 -> minimap = false
                95 -> vislevel = data.readUnsignedShort()
                97 -> resizeH = data.readUnsignedShort()
                98 -> resizeV = data.readUnsignedShort()
                99 -> alwaysOnTop = true
                100 -> ambient = data.readByte().toInt()
                101 -> contrast = data.readByte() * 5
                102 -> {
                    val enabledFlags = data.readUnsignedByte().toInt()
                    var count = 0
                    var bits = enabledFlags
                    while (bits != 0) {
                        bits = bits shr 1
                        count++
                    }
                    val groups = IntArray(count)
                    val files = IntArray(count)
                    for (i in 0 until count) {
                        if ((enabledFlags and (1 shl i)) == 0) {
                            groups[i] = -1
                            files[i] = -1
                        } else {
                            groups[i] = data.readNullableLargeSmart() ?: -1
                            files[i] = data.readUnsignedSmallSmartPlusOne()
                        }
                    }
                    this.headIconGraphic = CompactableIntArray(groups)
                    this.headIconIndex = CompactableIntArray(files)
                }
                103 -> turnSpeed = data.readUnsignedShort()
                106,
                118 -> {
                    val multiVarBit = data.readUnsignedShortOrNull()
                    val multiVarp = data.readUnsignedShortOrNull()
                    var defaultNpc: Int? = null
                    if (code == 118) {
                        defaultNpc = data.readUnsignedShortOrNull()
                    }
                    val count = data.readUnsignedByte().toInt()
                    val multiNpc = IntArray(count + 1) { 0 }
                    for (i in multiNpc.indices) {
                        multiNpc[i] = data.readUnsignedShortOrNull() ?: -1
                    }
                    this.multiVarp = multiVarp
                    this.multiVarBit = multiVarBit
                    this.multiNpcDefault = defaultNpc
                    this.multiNpc = CompactableIntArray(multiNpc)
                }
                107 -> active = false
                109 -> rotationFlag = false
                114 -> runAnim = data.readUnsignedShort()
                115 -> {
                    runAnim = data.readUnsignedShort()
                    runTurnBackAnim = data.readUnsignedShort()
                    runTurnLeftAnim = data.readUnsignedShort()
                    runTurnRightAnim = data.readUnsignedShort()
                }
                116 -> crawlAnim = data.readUnsignedShort()
                117 -> {
                    crawlAnim = data.readUnsignedShort()
                    crawlTurnBackAnim = data.readUnsignedShort()
                    crawlTurnLeftAnim = data.readUnsignedShort()
                    crawlTurnRightAnim = data.readUnsignedShort()
                }
                122 -> follower = true
                123 -> lowPriorityOps = true
                124 -> overlayHeight = data.readUnsignedShort()
                200 -> {
                    val id = data.readUnsignedByte().toInt()
                    moveRestrict = MoveRestrict.entries.first { it.id == id }
                }
                201 -> {
                    val id = data.readUnsignedByte().toInt()
                    defaultMode = NpcMode.entries.first { it.id == id }
                }
                202 -> {
                    val id = data.readUnsignedByte().toInt()
                    blockWalk = BlockWalk.entries.first { it.id == id }
                }
                203 -> {
                    val count = data.readUnsignedByte().toInt() + 1
                    val waypoints = mutableListOf<NpcPatrolWaypoint>()
                    repeat(count) {
                        val coords = data.readCoordGrid()
                        val pauseDelay = data.readUnsignedByte().toInt()
                        waypoints += NpcPatrolWaypoint(coords, pauseDelay)
                    }
                    this.patrol = NpcPatrol(waypoints)
                }
                204 -> respawnRate = data.readUnsignedShort()
                205 -> maxRange = data.readUnsignedByte().toInt()
                206 -> wanderRange = data.readUnsignedByte().toInt()
                207 -> attackRange = data.readUnsignedByte().toInt()
                208 -> huntRange = data.readUnsignedByte().toInt()
                209 -> huntMode = data.readUnsignedByte().toInt()
                210 -> giveChase = false
                211 -> timer = data.readUnsignedShort()
                212 -> {
                    val id = data.readUnsignedByte().toInt()
                    val dir = Direction.forId(id) ?: error("Invalid direction id `$id` for: $this.")
                    respawnDir = dir
                }
                213 -> contentGroup = data.readUnsignedShort()
                214 -> heroCount = data.readUnsignedShort()
                249 -> paramMap = ParamMap(data.readRawParams())
                else -> throw IOException("Error unrecognised .npc config code: $code")
            }
        }
}
