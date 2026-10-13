package org.runerealm.api.type.refs.timer

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
import org.runerealm.game.type.timer.TimerType

public class TimerReferenceResolver @Inject constructor(private val nameMapping: NameMapping) :
    NameTypeReferenceResolver<TimerType> {
    private val names: Map<String, Int>
        get() = nameMapping.timers

    override fun resolve(refs: NameTypeReferences<TimerType>): List<TypeReferenceResult> =
        refs.cache.map { it.resolve() }

    private fun TimerType.resolve(): TypeReferenceResult {
        val internalId = names[internalName] ?: return err(ImplicitNameNotFound(internalName))
        TypeResolver[this] = internalId
        return ok(FullSuccess)
    }
}
