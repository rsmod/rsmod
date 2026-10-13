package org.runerealm.api.type.refs.font

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.font.FontMetricsType
import org.runerealm.game.type.font.HashedFontMetricsType

public abstract class FontMetricsReferences :
    HashTypeReferences<FontMetricsType>(FontMetricsType::class.java) {
    override fun find(internal: String, hash: Long?): FontMetricsType {
        val type = HashedFontMetricsType(hash, internal)
        cache += type
        return type
    }
}
