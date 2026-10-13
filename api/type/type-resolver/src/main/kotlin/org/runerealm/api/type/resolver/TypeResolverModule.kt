package org.runerealm.api.type.resolver

import org.runerealm.module.ExtendedModule

public object TypeResolverModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeCleanup>()
        bindInstance<TypeResolver>()
    }
}
