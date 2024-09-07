package org.rsmod.api.type.refs

import org.rsmod.api.type.refs.resolver.TypeReferenceResolverMap
import org.rsmod.module.ExtendedModule

public object TypeReferenceModule : ExtendedModule() {
    override fun bind() {
        bindInstance<TypeReferenceResolverMap>()
    }
}
