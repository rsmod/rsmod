package org.rsmod.plugins.api.prot.desktop

import org.rsmod.plugins.api.cache.Archives.BASES
import org.rsmod.plugins.api.cache.Archives.BINARY
import org.rsmod.plugins.api.cache.Archives.CLIENT_SCRIPTS
import org.rsmod.plugins.api.cache.Archives.CONFIG
import org.rsmod.plugins.api.cache.Archives.DEFAULTS
import org.rsmod.plugins.api.cache.Archives.FONT_METRICS
import org.rsmod.plugins.api.cache.Archives.INSTRUMENTS
import org.rsmod.plugins.api.cache.Archives.INTERFACES
import org.rsmod.plugins.api.cache.Archives.JINGLES
import org.rsmod.plugins.api.cache.Archives.MAPS
import org.rsmod.plugins.api.cache.Archives.MODELS
import org.rsmod.plugins.api.cache.Archives.MUSIC
import org.rsmod.plugins.api.cache.Archives.SPRITES
import org.rsmod.plugins.api.cache.Archives.SYNTHS
import org.rsmod.plugins.api.cache.Archives.TEXTURES
import org.rsmod.plugins.api.cache.Archives.TOTAL_ARCHIVES
import org.rsmod.plugins.api.cache.Archives.UNNAMED_16
import org.rsmod.plugins.api.cache.Archives.UNNAMED_4
import org.rsmod.plugins.api.cache.Archives.VORBIS
import org.rsmod.plugins.api.cache.Archives.WORLD_MAP_DATA
import org.rsmod.plugins.api.cache.Archives.WORLD_MAP_GEO
import org.rsmod.plugins.api.cache.Archives.WORLD_MAP_GROUND
import org.rsmod.plugins.api.net.login.LoginPacketRequest
import org.rsmod.plugins.api.net.platform.login.LoginPlatformPacketDecoders
import org.rsmod.plugins.api.net.readIntAlt1
import org.rsmod.plugins.api.net.readIntAlt2
import org.rsmod.plugins.api.net.readIntAlt3_

private val platforms: LoginPlatformPacketDecoders by inject()
private val decoders = platforms.desktop

decoders.register { buf ->
    when (buf.readUnsignedByte().toInt()) {
        0 -> LoginPacketRequest.AuthType.TwoFactorCheckDeviceLinkFound
        1 -> LoginPacketRequest.AuthType.TwoFactorInputTrustDevice
        2 -> LoginPacketRequest.AuthType.TwoFactorCheckDeviceLinkNotFound
        3 -> LoginPacketRequest.AuthType.TwoFactorInputDoNotTrustDevice
        else -> LoginPacketRequest.AuthType.Skip
    }
}

decoders.register { buf ->
    val crcs = IntArray(TOTAL_ARCHIVES).apply {
        this[TEXTURES] = buf.readIntAlt3_()
        this[WORLD_MAP_GROUND] = buf.readIntAlt1()
        this[DEFAULTS] = buf.readIntAlt3_()
        this[BINARY] = buf.readIntAlt3_()
        this[FONT_METRICS] = buf.readIntAlt3_()
        this[MUSIC] = buf.readIntAlt3_()
        this[CLIENT_SCRIPTS] = buf.readIntAlt3_()
        this[WORLD_MAP_GEO] = buf.readInt()
        this[JINGLES] = buf.readIntAlt1()
        this[MAPS] = buf.readIntAlt3_()
        this[SYNTHS] = buf.readIntAlt1()
        this[UNNAMED_4] = buf.readInt()
        this[BASES] = buf.readIntAlt2()
        this[CONFIG] = buf.readIntAlt2()
        this[UNNAMED_16] = buf.readIntAlt1()
        this[VORBIS] = buf.readIntAlt2()
        this[INSTRUMENTS] = buf.readIntAlt1()
        this[WORLD_MAP_DATA] = buf.readIntAlt1()
        this[SPRITES] = buf.readIntAlt3_()
        this[INTERFACES] = buf.readIntAlt3_()
        this[MODELS] = buf.readInt()
    }
    return@register LoginPacketRequest.CacheCrc(crcs)
}
