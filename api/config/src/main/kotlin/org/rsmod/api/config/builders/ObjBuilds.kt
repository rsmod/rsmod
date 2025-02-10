package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.obj.ObjBuilder
import org.rsmod.game.type.obj.Dummyitem

internal object ObjBuilds : ObjBuilder() {
    init {
        build("template_for_transform") { dummyitem = Dummyitem.GraphicOnly }
    }
}
