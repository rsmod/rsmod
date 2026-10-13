package org.runerealm.api.testing.factory.obj

import org.runerealm.game.type.obj.ObjTypeBuilder
import org.runerealm.game.type.obj.UnpackedObjType

public class TestObjTypeFactory {
    public fun create(id: Int = 0, init: ObjTypeBuilder.() -> Unit = {}): UnpackedObjType {
        val builder =
            ObjTypeBuilder().apply {
                internal = "test_obj_type"
                desc = "$this"
            }
        return builder.apply(init).build(id)
    }
}
