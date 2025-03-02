package org.rsmod.api.type.builders.hitmark

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.UnpackedHitmarkType

public abstract class HitmarkBuilder : HashTypeBuilder<HitmarkTypeBuilder, UnpackedHitmarkType>() {
    override fun build(internal: String, init: HitmarkTypeBuilder.() -> Unit) {
        val type = HitmarkTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
