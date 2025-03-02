package org.rsmod.api.type.editors.hitmark

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.UnpackedHitmarkType

public abstract class HitmarkEditor : TypeEditor<HitmarkTypeBuilder, UnpackedHitmarkType>() {
    override fun edit(internal: String, init: HitmarkTypeBuilder.() -> Unit) {
        val type = HitmarkTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
