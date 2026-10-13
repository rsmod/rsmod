package org.runerealm.api.type.editors

import org.runerealm.api.type.editors.resolver.TypeEditorResolverMap
import org.runerealm.module.ExtendedModule

public object TypeEditorModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeEditorResolverMap>()
    }
}
