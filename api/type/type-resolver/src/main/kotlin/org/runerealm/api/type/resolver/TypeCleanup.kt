package org.runerealm.api.type.resolver

import jakarta.inject.Inject
import org.runerealm.api.type.builders.resolver.TypeBuilderResolverMap
import org.runerealm.api.type.editors.resolver.TypeEditorResolverMap
import org.runerealm.api.type.refs.resolver.TypeReferenceResolverMap

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
