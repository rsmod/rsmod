package org.rsmod.api.type.builders.resolver

import org.rsmod.api.type.builders.TypeBuilder

public fun interface TypeBuilderResolver<B, T> {
    public fun resolve(builders: TypeBuilder<B, T>): List<TypeBuilderResult>
}
