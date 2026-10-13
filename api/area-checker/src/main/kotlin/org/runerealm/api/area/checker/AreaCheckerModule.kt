package org.runerealm.api.area.checker

import org.runerealm.module.ExtendedModule

public object AreaCheckerModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AreaChecker>()
    }
}
