package org.runerealm.api.config.builders

import org.runerealm.api.type.builders.obj.ObjBuilder
import org.runerealm.game.type.obj.Dummyitem

internal object ObjBuilds : ObjBuilder() {
    init {
        build("template_for_transform") { dummyitem = Dummyitem.GraphicOnly }
    }
}
