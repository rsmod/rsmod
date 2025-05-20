package org.rsmod.api.area.checker

import org.rsmod.module.ExtendedModule

public object AreaCheckerModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AreaChecker>()
    }
}
