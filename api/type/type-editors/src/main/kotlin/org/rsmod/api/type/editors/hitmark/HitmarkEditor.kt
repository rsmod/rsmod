package org.rsmod.api.type.editors.hitmark

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.game.type.hitmark.HitmarkType
import org.rsmod.game.type.hitmark.HitmarkTypeBuilder
import org.rsmod.game.type.hitmark.UnpackedHitmarkType

public abstract class HitmarkEditor : TypeEditor<UnpackedHitmarkType>() {
    public fun edit(type: HitmarkType, init: HitmarkTypeBuilder.() -> Unit) {
        val type = HitmarkTypeBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
