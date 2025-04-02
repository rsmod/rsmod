package org.rsmod.api.type.verifier

import jakarta.inject.Inject
import org.rsmod.api.type.builders.resolver.TypeBuilderResolverMap
import org.rsmod.api.type.builders.resolver.TypeBuilderResult
import org.rsmod.api.type.verifier.TypeVerifier.ResolutionError
import org.rsmod.api.type.verifier.TypeVerifier.Verification

public class BuilderVerifier @Inject constructor(private val builders: TypeBuilderResolverMap) {
    public fun verifyAll(): Verification {
        val symbols = builders.symbolErrors
        if (symbols.isNotEmpty()) {
            val error = symbols.toError()
            return Verification.MissingSymbol(error)
        }

        val updates = builders.updates
        if (updates.isNotEmpty()) {
            val error = updates.toUpdateError()
            return Verification.CacheUpdateRequired(error)
        }

        val errors = builders.errors
        if (errors.isNotEmpty()) {
            val error = errors.toError()
            return Verification.Failure(error)
        }

        return Verification.FullSuccess
    }

    private fun List<TypeBuilderResult.Update<*>>.toUpdateError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toUpdateMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeBuilderResult.Update<*>>.toUpdateMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeBuilderResult.StatusUpdate.toHeader(count: Int): String =
        when (this) {
            TypeBuilderResult.CachePackRequired -> {
                "The following cache type builders need to be packed ($count found)"
            }
        }

    private fun <T> TypeBuilderResult.StatusUpdate.toMessage(value: T): String =
        when (this) {
            TypeBuilderResult.CachePackRequired -> "Build: $value"
        }

    private fun List<TypeBuilderResult.Error<*>>.toError(): ResolutionError {
        val mapped = groupBy { it.status.javaClass }
        val message = mapped.map { it.value.toErrMessage() }.joinToString(System.lineSeparator())
        return ResolutionError(message)
    }

    private fun List<TypeBuilderResult.Error<*>>.toErrMessage(): String =
        first().status.toHeader(size) +
            joinToString(
                separator = System.lineSeparator() + "\t- ",
                prefix = System.lineSeparator() + "\t- ",
                limit = MAX_ERROR_LINES,
            ) {
                it.status.toMessage(it.value)
            }

    private fun TypeBuilderResult.StatusErr.toHeader(count: Int): String =
        when (this) {
            is TypeBuilderResult.NameNotFound -> {
                "The following cache builders use names that are not defined in a names.sym " +
                    "file ($count found)"
            }
        }

    private fun <T> TypeBuilderResult.StatusErr.toMessage(value: T): String =
        when (this) {
            is TypeBuilderResult.NameNotFound -> "Name: \"$name\"\t| Build: $value"
        }

    private companion object {
        private const val MAX_ERROR_LINES: Int = 50
    }
}
