package org.runerealm.api.type.builders

import org.runerealm.api.type.builders.resolver.TypeBuilderResolverMap
import org.runerealm.module.ExtendedModule

public object TypeBuilderModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeBuilderResolverMap>()
    }
}
