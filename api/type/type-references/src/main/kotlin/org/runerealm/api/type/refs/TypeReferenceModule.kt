package org.runerealm.api.type.refs

import org.runerealm.api.type.refs.resolver.TypeReferenceResolverMap
import org.runerealm.module.ExtendedModule

public object TypeReferenceModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeReferenceResolverMap>()
    }
}
