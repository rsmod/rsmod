package org.rsmod.api.type.resolver

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.editors.resolver.TypeEditorResolverMap
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.api.type.refs.resolver.TypeReferenceResolverMap

public class TypeResolver
@Inject
constructor(
    private val references: TypeReferenceResolverMap,
    private val builders: TypeBuilderResolverMap,
    private val editors: TypeEditorResolverMap,
) {
    public val referenceCount: Int
        get() = references.size

    public val builderCount: Int
        get() = builders.size

    public val editorCount: Int
        get() = editors.size

    public fun appendReferences(refs: Collection<TypeReferences<*, *>>) {
        this.references += refs
    }

    public fun appendBuilders(builders: Collection<TypeBuilder<*, *>>) {
        this.builders += builders
    }

    public fun appendEditors(editors: Collection<TypeEditor<*>>) {
        this.editors += editors
    }

    public fun resolveReferences() {
        references.resolveAll()
    }

    public fun resolveBuilders() {
        builders.resolveAll()
    }

    public fun resolveEditors() {
        editors.resolveAll()
    }
}
