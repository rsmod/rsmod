package org.rsmod.api.type.editors

import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.module.ExtendedModule

public object TypeEditorModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeEditorResolverMap>()
    }
}
