package org.runerealm.api.type.refs.midi

import jakarta.inject.Inject
import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.api.type.refs.resolver.NameTypeReferenceResolver
import org.runerealm.api.type.refs.resolver.TypeReferenceResult
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.FullSuccess
import org.runerealm.api.type.refs.resolver.TypeReferenceResult.ImplicitNameNotFound
import org.runerealm.api.type.refs.resolver.err
import org.runerealm.api.type.refs.resolver.ok
import org.runerealm.api.type.symbols.name.NameMapping
import org.runerealm.game.type.TypeResolver
import org.runerealm.game.type.midi.MidiType

public class MidiReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<MidiType> {
    private val names: Map<String, Int>
        get() = nameMapping.midis

    override fun resolve(refs: NameTypeReferences<MidiType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun MidiType.resolve(): TypeReferenceResult {
        val internalId = names[internalName] ?: return err(ImplicitNameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
