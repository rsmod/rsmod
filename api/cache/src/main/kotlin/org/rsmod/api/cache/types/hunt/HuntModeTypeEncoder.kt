package org.rsmod.api.cache.types.hunt

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.api.cache.util.readOrNull
import org.rsmod.api.cache.util.writeNullableShort
import org.rsmod.game.type.hunt.HuntModeTypeBuilder
import org.rsmod.game.type.hunt.UnpackedHuntModeType

public object HuntModeTypeEncoder {
    public fun encodeAll(cache: Cache, types: Iterable<UnpackedHuntModeType>, ctx: EncoderContext) {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.HUNT
        for (type in types) {
            val oldBuf = cache.readOrNull(archive, config, type.id)
            val newBuf =
                buffer.clear().encodeConfig {
                    if (ctx.encodeFull) {
                        encodeGame(type, this)
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
            }
            oldBuf?.release()
        }
        buffer.release()
    }

    public fun encodeGame(type: UnpackedHuntModeType, data: ByteBuf): Unit =
        with(type) {
            if (this.type != HuntModeTypeBuilder.DEFAULT_TYPE) {
                data.writeByte(1)
                data.writeByte(this.type.id)
            }

            if (checkVis != HuntModeTypeBuilder.DEFAULT_VIS) {
                data.writeByte(2)
                data.writeByte(checkVis.id)
            }

            if (checkNotTooStrong != HuntModeTypeBuilder.DEFAULT_NOTTOOSTRONG) {
                data.writeByte(3)
                data.writeByte(checkNotTooStrong.id)
            }

            if (checkNotBusy) {
                data.writeByte(4)
            }

            if (findKeepHunting) {
                data.writeByte(5)
            }

            if (findNewMode != HuntModeTypeBuilder.DEFAULT_NEWMODE) {
                data.writeByte(6)
                data.writeByte(findNewMode.id)
            }

            if (nobodyNear != HuntModeTypeBuilder.DEFAULT_NOBODYNEAR) {
                data.writeByte(7)
                data.writeByte(nobodyNear.id)
            }

            if (checkNotCombat != HuntModeTypeBuilder.DEFAULT_CHECKNOTCOMBAT) {
                data.writeByte(8)
                data.writeShort(checkNotCombat)
            }

            if (checkNotCombatSelf != HuntModeTypeBuilder.DEFAULT_CHECKNOTCOMBAT_SELF) {
                data.writeByte(9)
                data.writeShort(checkNotCombatSelf)
            }

            if (!checkAfk) {
                data.writeByte(10)
            }

            if (rate != HuntModeTypeBuilder.DEFAULT_RATE) {
                data.writeByte(11)
                data.writeShort(rate)
            }

            checkNpc?.let {
                data.writeByte(12)
                data.writeNullableShort(it.npc)
                data.writeNullableShort(it.category)
            }

            checkObj?.let {
                data.writeByte(13)
                data.writeNullableShort(it.obj)
                data.writeNullableShort(it.category)
            }

            checkLoc?.let {
                data.writeByte(14)
                data.writeNullableShort(it.loc)
                data.writeNullableShort(it.category)
            }

            checkInvObj?.let {
                data.writeByte(15)
                data.writeShort(it.inv)
                data.writeShort(it.type)
                data.writeByte(it.operator.id)
                data.writeInt(it.required)
            }

            checkInvParam?.let {
                data.writeByte(16)
                data.writeShort(it.inv)
                data.writeShort(it.type)
                data.writeByte(it.operator.id)
                data.writeInt(it.required)
            }

            checkVar1?.let {
                data.writeByte(17)
                data.writeShort(it.varp)
                data.writeByte(it.operator.id)
                data.writeInt(it.required)
            }

            checkVar2?.let {
                data.writeByte(18)
                data.writeShort(it.varp)
                data.writeByte(it.operator.id)
                data.writeInt(it.required)
            }

            checkVar3?.let {
                data.writeByte(19)
                data.writeShort(it.varp)
                data.writeByte(it.operator.id)
                data.writeInt(it.required)
            }
        }
}
