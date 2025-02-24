package org.rsmod.api.type.refs.font

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.font.FontMetricsType
import org.rsmod.game.type.font.HashedFontMetricsType

public abstract class FontMetricsReferences :
    HashTypeReferences<FontMetricsType>(FontMetricsType::class.java) {
    override fun find(internal: String, hash: Long?): FontMetricsType {
        val type = HashedFontMetricsType(hash, internal)
        cache += type
        return type
    }
}
