package org.rsmod.plugins.api.cache.packer.npc

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.cache.GameCache
import org.rsmod.game.model.npc.type.NpcType
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import org.rsmod.plugins.api.cache.config.npc.NpcConfig
import org.rsmod.plugins.api.cache.config.npc.NpcConfigLoader
import org.rsmod.plugins.api.cache.config.toConfigMapper
import org.rsmod.plugins.api.cache.toResourceUrl
import org.rsmod.plugins.api.cache.writeParameters
import org.rsmod.plugins.api.util.toPlural
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

private val logger = InlineLogger()

private const val NPC_ARCHIVE = 2
private const val NPC_GROUP = 9

class NpcTypePacker @Inject constructor(
    private val mapper: ObjectMapper,
    private val cache: GameCache,
    private val files: NamedConfigFileMap,
    private val types: NpcTypeList
) {

    fun pack() {
        val files = files.getValue(DefaultExtensions.NPC_CONFIGS)
        val loader = NpcConfigLoader(mapper.toConfigMapper())
        val configs = loader.loadAll(files).filter { it.pack }
        pack(configs)
        logger.info { "Packed ${types.size} npc config ${"file".toPlural(types.size)} into game cache" }
    }

    fun pack(configs: Iterable<NpcConfig>) {
        val archive = cache.archive(NPC_ARCHIVE)
        val group = cache.group(archive, NPC_GROUP)
        var repackArchive = false
        configs.forEach { cfg ->
            val newData = cfg.toByteBuf() ?: return@forEach
            val oldData = cache.file(group, cfg.id)
            if (oldData == newData) return@forEach
            cache.putFile(group, cfg.id, newData)
            repackArchive = true
        }
        if (repackArchive) {
            cache.packGroup(archive, group)
            cache.packArchive(archive)
        }
    }

    private fun NpcConfig.toByteBuf(): ByteBuf? {
        return if (dataFile == null) {
            inherit?.let {
                /* append inherited properties from respective cache type */
                builder += types[it]
            }
            val type = builder.build()
            if (type == types[type.id]) {
                /* if no change is found, don't bother repacking */
                return null
            }
            Unpooled.buffer().apply { type.writeTo(this) }
        } else {
            val resource = dataFile.toResourceUrl()
                ?: throw FileNotFoundException("File not found in resources: $dataFile")
            val file = File(resource.path)
            val input = file.inputStream()
            Unpooled.wrappedBuffer(input.readAllBytes())
        }
    }

    private fun NpcType.writeTo(buf: ByteBuf) {
        if (models.isNotEmpty()) {
            buf.writeByte(1)
            buf.writeByte(models.size)
            models.forEach(buf::writeShort)
        }
        if (name != "null") {
            buf.writeByte(2)
            buf.writeStringCP1252(name)
        }
        if (size != 1) {
            buf.writeByte(12)
            buf.writeByte(size)
        }
        if (readyAnim != -1) {
            buf.writeByte(13)
            buf.writeShort(readyAnim)
        }
        if (walkAnim != -1) {
            val otherWalkAnims = walkLeftAnim != -1 || walkRightAnim != -1 || walkBackAnim != -1
            if (!otherWalkAnims) {
                buf.writeByte(14)
                buf.writeShort(walkAnim)
            } else {
                buf.writeByte(17)
                buf.writeShort(walkAnim)
                buf.writeShort(walkBackAnim)
                buf.writeShort(walkLeftAnim)
                buf.writeShort(walkRightAnim)
            }
        }
        if (turnLeftAnim != -1) {
            buf.writeByte(15)
            buf.writeShort(turnLeftAnim)
        }
        if (turnRightAnim != -1) {
            buf.writeByte(16)
            buf.writeShort(turnRightAnim)
        }
        options.forEachIndexed { index, opt ->
            if (opt != null && opt != "Hidden") {
                buf.writeByte(30 + index)
                buf.writeStringCP1252(opt)
            }
        }
        if (recolorSrc.isNotEmpty()) {
            buf.writeByte(40)
            buf.writeByte(recolorSrc.size)
            for (i in recolorSrc.indices) {
                buf.writeShort(recolorSrc[i])
                buf.writeShort(recolorDest[i])
            }
        }
        if (retextureSrc.isNotEmpty()) {
            buf.writeByte(41)
            buf.writeByte(retextureSrc.size)
            for (i in retextureSrc.indices) {
                buf.writeShort(retextureSrc[i])
                buf.writeShort(retextureDest[i])
            }
        }
        if (headModels.isNotEmpty()) {
            buf.writeByte(60)
            buf.writeByte(headModels.size)
            headModels.forEach(buf::writeShort)
        }
        if (!minimapVisible) {
            buf.writeByte(93)
        }
        if (level != -1) {
            buf.writeByte(95)
            buf.writeShort(level)
        }
        if (resizeX != 128) {
            buf.writeByte(97)
            buf.writeShort(resizeX)
        }
        if (resizeY != 128) {
            buf.writeByte(98)
            buf.writeShort(resizeY)
        }
        if (renderPriority) {
            buf.writeByte(99)
        }
        if (ambient != 0) {
            buf.writeByte(100)
            buf.writeByte(ambient)
        }
        if (contrast != 0) {
            buf.writeByte(101)
            buf.writeByte(contrast / 5)
        }
        if (headIcon != -1) {
            buf.writeByte(102)
            buf.writeShort(headIcon)
        }
        if (rotation != 32) {
            buf.writeByte(103)
            buf.writeShort(rotation)
        }
        if (transforms.isNotEmpty()) {
            val instruction = if (defaultTransform != -1) 118 else 106
            buf.writeByte(instruction)
            buf.writeShort(if (varbit == -1) 65535 else varbit)
            buf.writeShort(if (varp == -1) 65535 else varp)
            if (defaultTransform != -1) {
                buf.writeShort(if (defaultTransform == -1) 65535 else defaultTransform)
            }
            val count = transforms.size - 2
            for (i in 0..count) {
                val transform = transforms[i]
                buf.writeShort(if (transform == -1) 65535 else transform)
            }
        }
        if (!interact) {
            buf.writeByte(107)
        }
        if (!clickable) {
            buf.writeByte(109)
        }
        if (aBoolean3532) {
            buf.writeByte(111)
        }
        if (parameters.isNotEmpty()) {
            buf.writeByte(249)
            buf.writeParameters(parameters)
        }
        buf.writeByte(0)
    }
}
