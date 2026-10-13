package org.runerealm.api.type.builders.resolver

import org.runerealm.api.type.builders.TypeBuilder

public fun interface TypeBuilderResolver<B, T> {
    public fun resolve(builders: TypeBuilder<B, T>): List<TypeBuilderResult>
}
