package org.rsmod.api.type.resolver

import org.rsmod.module.ExtendedModule

public object TypeResolverModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeCleanup>()
        bindInstance<TypeResolver>()
    }
}
