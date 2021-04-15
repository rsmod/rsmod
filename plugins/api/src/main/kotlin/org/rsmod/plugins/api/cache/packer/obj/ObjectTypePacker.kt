package org.rsmod.plugins.api.cache.packer.obj

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.cache.GameCache
import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.obj.type.ObjectType
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import org.rsmod.plugins.api.cache.config.obj.ObjectConfig
import org.rsmod.plugins.api.cache.config.obj.ObjectConfigLoader
import org.rsmod.plugins.api.cache.config.toConfigMapper
import org.rsmod.plugins.api.cache.writeParameters
import org.rsmod.plugins.api.util.toPlural
import java.io.FileNotFoundException
import java.nio.file.Files
import javax.inject.Inject

private val logger = InlineLogger()

private const val OBJ_ARCHIVE = 2
private const val OBJ_GROUP = 6

class ObjectTypePacker @Inject constructor(
    private val mapper: ObjectMapper,
    private val config: GameConfig,
    private val cache: GameCache,
    private val files: NamedConfigFileMap,
    private val types: ObjectTypeList
) {

    fun pack() {
        val files = files.getValue(DefaultExtensions.OBJ_CONFIGS)
        val loader = ObjectConfigLoader(mapper.toConfigMapper())
        val configs = loader.loadAll(files).filter { it.pack }
        pack(configs)
        logger.info { "Packed ${configs.size} object config ${"file".toPlural(configs.size)} into game cache" }
    }

    fun pack(configs: Iterable<ObjectConfig>) {
        val archive = cache.archive(OBJ_ARCHIVE)
        val group = cache.group(archive, OBJ_GROUP)
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

    private fun ObjectConfig.toByteBuf(): ByteBuf? {
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
            val file = config.pluginConfigPath.resolve(dataFile)
            if (!Files.exists(file)) throw FileNotFoundException("File not found in resources: $dataFile")
            Files.newInputStream(file).use { input ->
                Unpooled.wrappedBuffer(input.readAllBytes())
            }
        }
    }

    private fun ObjectType.writeTo(buf: ByteBuf) {
        if (models.isNotEmpty() && modelTypes.isNotEmpty()) {
            buf.writeByte(1)
            buf.writeByte(models.size)
            for (i in models.indices) {
                buf.writeShort(models[i])
                buf.writeByte(modelTypes[i])
            }
        }
        if (name != "null") {
            buf.writeByte(2)
            buf.writeStringCP1252(name)
        }
        if (models.isNotEmpty() && modelTypes.isEmpty()) {
            buf.writeByte(5)
            buf.writeByte(models.size)
            models.forEach(buf::writeShort)
        }
        if (width != 1) {
            buf.writeByte(14)
            buf.writeByte(width)
        }
        if (height != 1) {
            buf.writeByte(15)
            buf.writeByte(height)
        }
        if (!blockPath) {
            buf.writeByte(17)
        }
        if (!blockProjectile) {
            buf.writeByte(18)
        }
        if (interactType != -1) {
            buf.writeByte(19)
            buf.writeByte(interactType)
        }
        if (contouredGround == 0) {
            buf.writeByte(21)
        }
        if (nonFlatShading) {
            buf.writeByte(22)
        }
        if (clippedModel) {
            buf.writeByte(23)
        }
        animation.let {
            buf.writeByte(24)
            buf.writeShort(if (it == -1) 65535 else it)
        }
        if (clipType == 1) {
            buf.writeByte(27)
        }
        if (decorDisplacement != 16) {
            buf.writeByte(28)
            buf.writeByte(decorDisplacement)
        }
        if (ambient != 0) {
            buf.writeByte(29)
            buf.writeByte(ambient)
        }
        if (contrast != 0) {
            buf.writeByte(39)
            buf.writeByte(contrast)
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
            buf.writeByte(40)
            buf.writeByte(retextureSrc.size)
            for (i in retextureSrc.indices) {
                buf.writeShort(retextureSrc[i])
                buf.writeShort(retextureDest[i])
            }
        }
        if (rotated) {
            buf.writeByte(62)
        }
        if (!clipped) {
            buf.writeByte(64)
        }
        if (resizeX != 128) {
            buf.writeByte(65)
            buf.writeShort(resizeX)
        }
        if (resizeHeight != 128) {
            buf.writeByte(66)
            buf.writeShort(resizeHeight)
        }
        if (resizeY != 128) {
            buf.writeByte(67)
            buf.writeShort(resizeY)
        }
        if (mapSceneId != -1) {
            buf.writeByte(68)
            buf.writeShort(mapSceneId)
        }
        if (clipMask != 0) {
            buf.writeByte(69)
            buf.writeByte(clipMask)
        }
        if (offsetX != 0) {
            buf.writeByte(70)
            buf.writeShort(offsetX)
        }
        if (offsetHeight != 0) {
            buf.writeByte(71)
            buf.writeShort(offsetHeight)
        }
        if (offsetY != 0) {
            buf.writeByte(72)
            buf.writeShort(offsetY)
        }
        if (obstruct) {
            buf.writeByte(73)
        }
        if (hollow) {
            buf.writeByte(74)
        }
        if (supportItems != -1) {
            buf.writeByte(75)
            buf.writeByte(supportItems)
        }
        if (transforms.isNotEmpty()) {
            val instruction = if (defaultTransform != -1) 92 else 77
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
        if (ambientSoundId != -1) {
            buf.writeByte(78)
            buf.writeShort(ambientSoundId)
            buf.writeByte(ambientSoundRadius)
        }
        if (anIntArray3428.isNotEmpty()) {
            buf.writeByte(79)
            buf.writeShort(anInt3426)
            buf.writeShort(anInt3427)
            buf.writeByte(ambientSoundRadius)
            buf.writeByte(anIntArray3428.size)
            anIntArray3428.forEach(buf::writeShort)
        }
        if (contouredGround != -1 && contouredGround != 0) {
            buf.writeByte(81)
            buf.writeByte(contouredGround / 256)
        }
        if (mapIconId != -1) {
            buf.writeByte(82)
            buf.writeShort(mapIconId)
        }
        if (!aBoolean3429) {
            buf.writeByte(89)
        }
        if (parameters.isNotEmpty()) {
            buf.writeByte(249)
            buf.writeParameters(parameters)
        }
        buf.writeByte(0)
    }
}
