package org.runerealm.api.type.editors.hitmark

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.game.type.hitmark.HitmarkType
import org.runerealm.game.type.hitmark.HitmarkTypeBuilder
import org.runerealm.game.type.hitmark.UnpackedHitmarkType

public abstract class HitmarkEditor : TypeEditor<UnpackedHitmarkType>() {
    public fun edit(type: HitmarkType, init: HitmarkTypeBuilder.() -> Unit) {
        val type = HitmarkTypeBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
