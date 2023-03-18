package org.rsmod.plugins.api.prot.desktop

import org.openrs2.buffer.readIntAlt3
import org.openrs2.buffer.readIntAlt3Reverse
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
import org.rsmod.plugins.api.net.platform.LoginPlatformPacketDecoders

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
        this[TEXTURES] = buf.readIntAlt3()
        this[WORLD_MAP_GROUND] = buf.readIntLE()
        this[DEFAULTS] = buf.readIntAlt3()
        this[BINARY] = buf.readIntAlt3()
        this[FONT_METRICS] = buf.readIntAlt3()
        this[MUSIC] = buf.readIntAlt3()
        this[CLIENT_SCRIPTS] = buf.readIntAlt3()
        this[WORLD_MAP_GEO] = buf.readInt()
        this[JINGLES] = buf.readIntLE()
        this[MAPS] = buf.readIntAlt3()
        this[SYNTHS] = buf.readIntLE()
        this[UNNAMED_4] = buf.readInt()
        this[BASES] = buf.readIntAlt3Reverse()
        this[CONFIG] = buf.readIntAlt3Reverse()
        this[UNNAMED_16] = buf.readIntLE()
        this[VORBIS] = buf.readIntAlt3Reverse()
        this[INSTRUMENTS] = buf.readIntLE()
        this[WORLD_MAP_DATA] = buf.readIntLE()
        this[SPRITES] = buf.readIntAlt3()
        this[INTERFACES] = buf.readIntAlt3()
        this[MODELS] = buf.readInt()
    }
    return@register LoginPacketRequest.CacheCrc(crcs)
}
