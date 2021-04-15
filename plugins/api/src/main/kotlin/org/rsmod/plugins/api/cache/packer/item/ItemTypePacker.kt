package org.rsmod.plugins.api.cache.packer.item

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.cache.GameCache
import org.rsmod.game.config.GameConfig
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.plugins.api.cache.config.file.DefaultExtensions
import org.rsmod.plugins.api.cache.config.file.NamedConfigFileMap
import org.rsmod.plugins.api.cache.config.item.ItemConfig
import org.rsmod.plugins.api.cache.config.item.ItemConfigLoader
import org.rsmod.plugins.api.cache.config.toConfigMapper
import org.rsmod.plugins.api.cache.writeParameters
import org.rsmod.plugins.api.util.toPlural
import java.io.FileNotFoundException
import java.nio.file.Files
import javax.inject.Inject

private val logger = InlineLogger()

private const val ITEM_ARCHIVE = 2
private const val ITEM_GROUP = 10

class ItemTypePacker @Inject constructor(
    private val mapper: ObjectMapper,
    private val config: GameConfig,
    private val cache: GameCache,
    private val files: NamedConfigFileMap,
    private val types: ItemTypeList
) {

    fun pack() {
        val files = files.getValue(DefaultExtensions.ITEM_CONFIGS)
        val loader = ItemConfigLoader(mapper.toConfigMapper())
        val configs = loader.loadAll(files).filter { it.pack }
        pack(configs)
        logger.info { "Packed ${types.size} item config ${"file".toPlural(types.size)} into game cache" }
    }

    fun pack(configs: Iterable<ItemConfig>) {
        val archive = cache.archive(ITEM_ARCHIVE)
        val group = cache.group(archive, ITEM_GROUP)
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

    private fun ItemConfig.toByteBuf(): ByteBuf? {
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

    private fun ItemType.writeTo(buf: ByteBuf) {
        if (model != 0) {
            buf.writeByte(1)
            buf.writeShort(model)
        }
        if (name != "null") {
            buf.writeByte(2)
            buf.writeStringCP1252(name)
        }
        if (zoom2d != 2000) {
            buf.writeByte(4)
            buf.writeShort(zoom2d)
        }
        if (xan2d != 0) {
            buf.writeByte(5)
            buf.writeShort(xan2d)
        }
        if (yan2d != 0) {
            buf.writeByte(6)
            buf.writeShort(yan2d)
        }
        if (xoff2d != 0) {
            buf.writeByte(7)
            buf.writeShort(xoff2d)
        }
        if (yoff2d != 0) {
            buf.writeByte(8)
            buf.writeShort(yoff2d)
        }
        if (stacks) {
            buf.writeByte(11)
        }
        if (cost != 1) {
            buf.writeByte(12)
            buf.writeInt(cost)
        }
        if (members) {
            buf.writeByte(16)
        }
        if (maleModel0 != 0) {
            buf.writeByte(23)
            buf.writeShort(maleModel0)
            buf.writeByte(maleModelOffset)
        }
        if (maleModel1 != 0) {
            buf.writeByte(24)
            buf.writeShort(maleModel1)
        }
        if (femaleModel0 != 0) {
            buf.writeByte(25)
            buf.writeShort(femaleModel0)
            buf.writeByte(femaleModelOffset)
        }
        if (femaleModel1 != 0) {
            buf.writeByte(26)
            buf.writeShort(femaleModel1)
        }
        groundOptions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Take") {
                buf.writeByte(30 + i)
                buf.writeStringCP1252(str)
            }
        }
        inventoryOptions.forEachIndexed { i, str ->
            if (str != null && str != "Hidden" && str != "Drop") {
                buf.writeByte(35 + i)
                buf.writeStringCP1252(str)
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
        if (dropOptionIndex != -2) {
            buf.writeByte(42)
            buf.writeByte(dropOptionIndex)
        }
        if (exchangeable) {
            buf.writeByte(65)
        }
        if (maleModel2 != 0) {
            buf.writeByte(78)
            buf.writeShort(maleModel2)
        }
        if (femaleModel2 != 0) {
            buf.writeByte(79)
            buf.writeShort(femaleModel2)
        }
        if (maleHeadModel0 != 0) {
            buf.writeByte(90)
            buf.writeShort(maleHeadModel0)
        }
        if (femaleHeadModel0 != 0) {
            buf.writeByte(91)
            buf.writeShort(femaleHeadModel0)
        }
        if (maleHeadModel1 != 0) {
            buf.writeByte(92)
            buf.writeShort(maleHeadModel1)
        }
        if (femaleHeadModel1 != 0) {
            buf.writeByte(93)
            buf.writeShort(femaleHeadModel1)
        }
        if (zan2d != 0) {
            buf.writeByte(95)
            buf.writeShort(zan2d)
        }
        if (noteLink != 0) {
            buf.writeByte(97)
            buf.writeShort(noteLink)
        }
        if (noteValue != 0) {
            buf.writeByte(98)
            buf.writeShort(noteValue)
        }
        countItem.forEachIndexed { i, obj ->
            val countCo = countCo[i]
            buf.writeByte(100 + i)
            buf.writeShort(obj)
            buf.writeShort(countCo)
        }
        if (resizeX != 128) {
            buf.writeByte(110)
            buf.writeShort(resizeX)
        }
        if (resizeY != 128) {
            buf.writeByte(111)
            buf.writeShort(resizeY)
        }
        if (resizeZ != 128) {
            buf.writeByte(112)
            buf.writeShort(resizeZ)
        }
        if (ambient != 0) {
            buf.writeByte(113)
            buf.writeByte(ambient)
        }
        if (contrast != 0) {
            buf.writeByte(114)
            buf.writeByte(contrast)
        }
        if (teamCape != 0) {
            buf.writeByte(115)
            buf.writeByte(teamCape)
        }
        if (boughtLink != 0) {
            buf.writeByte(139)
            buf.writeShort(boughtLink)
        }
        if (boughtValue != 0) {
            buf.writeByte(140)
            buf.writeShort(boughtValue)
        }
        if (placeholderLink != 0) {
            buf.writeByte(148)
            buf.writeShort(placeholderLink)
        }
        if (placeholderValue != 0) {
            buf.writeByte(149)
            buf.writeShort(placeholderValue)
        }
        if (parameters.isNotEmpty()) {
            buf.writeByte(249)
            buf.writeParameters(parameters)
        }
        buf.writeByte(0)
    }
}
