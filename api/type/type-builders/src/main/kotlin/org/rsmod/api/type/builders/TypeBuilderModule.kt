package org.rsmod.api.type.builders

import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.module.ExtendedModule

public object TypeBuilderModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeBuilderResolverMap>()
    }
}
