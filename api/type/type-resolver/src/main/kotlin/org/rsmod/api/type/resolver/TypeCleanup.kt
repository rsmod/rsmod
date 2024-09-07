package org.rsmod.api.type.resolver

import jakarta.inject.Inject
import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.api.type.refs.resolver.TypeReferenceResolverMap

public class TypeCleanup
@Inject
constructor(
    private val references: TypeReferenceResolverMap,
    private val builders: TypeBuilderResolverMap,
    private val editors: TypeEditorResolverMap,
) {
    public fun clearAll() {
        references.clear()
        builders.clear()
        editors.clear()
    }
}
