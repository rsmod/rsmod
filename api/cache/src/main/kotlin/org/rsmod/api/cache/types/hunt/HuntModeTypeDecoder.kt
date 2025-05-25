package org.rsmod.api.cache.types.hunt

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.IOException
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.TextUtil
import org.rsmod.api.cache.util.readUnsignedShortOrNull
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.hunt.HuntCheckNotTooStrong
import org.rsmod.game.type.hunt.HuntCondition
import org.rsmod.game.type.hunt.HuntModeTypeBuilder
import org.rsmod.game.type.hunt.HuntModeTypeList
import org.rsmod.game.type.hunt.HuntNobodyNear
import org.rsmod.game.type.hunt.HuntType
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.hunt.UnpackedHuntModeType

public object HuntModeTypeDecoder {
    public fun decodeAll(cache: Cache): HuntModeTypeList {
        val types = Int2ObjectOpenHashMap<UnpackedHuntModeType>()
        val files = cache.list(Js5Archives.CONFIG, Js5Configs.HUNT)
        for (file in files) {
            val data = cache.read(Js5Archives.CONFIG, Js5Configs.HUNT, file.id)
            val type = data.use { decode(it).build(file.id) }
            types[file.id] = type.apply { TypeResolver[this] = file.id }
        }
        return HuntModeTypeList(types)
    }

    public fun decode(data: ByteBuf): HuntModeTypeBuilder {
        val builder = HuntModeTypeBuilder(TextUtil.NULL)
        while (data.isReadable) {
            val code = data.readUnsignedByte().toInt()
            if (code == 0) {
                break
            }
            decode(builder, data, code)
        }
        return builder
    }

    public fun decode(builder: HuntModeTypeBuilder, data: ByteBuf, code: Int): Unit =
        with(builder) {
            when (code) {
                1 -> {
                    val id = data.readUnsignedByte().toInt()
                    val type = HuntType[id] ?: error("Invalid HuntType: $id")
                    this.type = type
                }
                2 -> {
                    val id = data.readUnsignedByte().toInt()
                    val vis = HuntVis[id] ?: error("Invalid HuntVis: $id")
                    this.checkVis = vis
                }
                3 -> {
                    val id = data.readUnsignedByte().toInt()
                    val check = HuntCheckNotTooStrong[id] ?: error("Invalid NotTooStrong: $id")
                    this.checkNotTooStrong = check
                }
                4 -> checkNotBusy = true
                5 -> findKeepHunting = true
                6 -> {
                    val id = data.readUnsignedByte().toInt()
                    val mode = NpcMode[id] ?: error("Invalid NpcMode: $id")
                    this.findNewMode = mode
                }
                7 -> {
                    val id = data.readUnsignedByte().toInt()
                    val nobodyNear = HuntNobodyNear[id] ?: error("Invalid NobodyNear: $id")
                    this.nobodyNear = nobodyNear
                }
                8 -> checkNotCombat = data.readUnsignedShort()
                9 -> checkNotCombatSelf = data.readUnsignedShort()
                10 -> checkAfk = false
                11 -> rate = data.readUnsignedShort()
                12 -> {
                    val type = data.readUnsignedShortOrNull()
                    val category = data.readUnsignedShortOrNull()
                    val condition = HuntCondition.NpcCondition(type, category)
                    this.checkNpc = condition
                }
                13 -> {
                    val type = data.readUnsignedShortOrNull()
                    val category = data.readUnsignedShortOrNull()
                    val condition = HuntCondition.ObjCondition(type, category)
                    this.checkObj = condition
                }
                14 -> {
                    val type = data.readUnsignedShortOrNull()
                    val category = data.readUnsignedShortOrNull()
                    val condition = HuntCondition.LocCondition(type, category)
                    this.checkLoc = condition
                }
                15 -> {
                    val inv = data.readUnsignedShort()
                    val obj = data.readUnsignedShort()
                    val op = data.readUnsignedByte().toInt()
                    val value = data.readInt()
                    val operator = HuntCondition.Operator[op] ?: error("Invalid Operator: $op")
                    this.checkInvObj = HuntCondition.InvCondition(inv, obj, operator, value)
                }
                16 -> {
                    val inv = data.readUnsignedShort()
                    val param = data.readUnsignedShort()
                    val op = data.readUnsignedByte().toInt()
                    val value = data.readInt()
                    val operator = HuntCondition.Operator[op] ?: error("Invalid Operator: $op")
                    this.checkInvParam = HuntCondition.InvCondition(inv, param, operator, value)
                }
                17,
                18,
                19 -> {
                    val varp = data.readUnsignedShort()
                    val op = data.readUnsignedByte().toInt()
                    val value = data.readInt()
                    val operator = HuntCondition.Operator[op] ?: error("Invalid Operator: $op")
                    val condition = HuntCondition.VarCondition(varp, operator, value)
                    when (code) {
                        17 -> checkVar1 = condition
                        18 -> checkVar2 = condition
                        19 -> checkVar3 = condition
                        else -> throw NotImplementedError("Unhandled code: $code")
                    }
                }
                else -> throw IOException("Error unrecognised .hunt config code: $code")
            }
        }
}
