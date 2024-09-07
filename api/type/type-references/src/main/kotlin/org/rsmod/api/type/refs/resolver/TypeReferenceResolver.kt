package org.rsmod.api.type.refs.resolver

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.api.type.refs.TypeReferences

public fun interface TypeReferenceResolver<T, I> {
    public fun resolve(refs: TypeReferences<T, I>): List<TypeReferenceResult>
}

public interface HashTypeReferenceResolver<T> : TypeReferenceResolver<T, Long> {
    public fun resolve(refs: HashTypeReferences<T>): List<TypeReferenceResult>

    override fun resolve(refs: TypeReferences<T, Long>): List<TypeReferenceResult> =
        resolve(refs as HashTypeReferences<T>)
}

public interface NameTypeReferenceResolver<T> : TypeReferenceResolver<T, String> {
    public fun resolve(refs: NameTypeReferences<T>): List<TypeReferenceResult>

    override fun resolve(refs: TypeReferences<T, String>): List<TypeReferenceResult> =
        resolve(refs as NameTypeReferences<T>)
}
