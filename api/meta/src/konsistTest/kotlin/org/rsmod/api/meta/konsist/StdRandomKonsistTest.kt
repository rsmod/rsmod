package org.rsmod.api.meta.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoParameterDeclaration
import com.lemonappdev.konsist.api.declaration.KoPropertyDeclaration
import com.lemonappdev.konsist.api.declaration.KoVariableDeclaration
import com.lemonappdev.konsist.api.ext.list.getters
import com.lemonappdev.konsist.api.ext.list.parameters
import com.lemonappdev.konsist.api.ext.list.setters
import com.lemonappdev.konsist.api.ext.list.variables
import com.lemonappdev.konsist.api.provider.KoNonNullableTypeProvider
import com.lemonappdev.konsist.api.provider.KoNullableTypeProvider
import com.lemonappdev.konsist.api.provider.KoTacitTypeProvider
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.core.exception.KoInternalException
import kotlin.random.Random
import org.junit.jupiter.api.Test

/**
 * This test ensures that the framework exclusively uses its own [org.rsmod.api.random.GameRandom]
 * implementation rather than the standard library's [kotlin.random.Random] or [java.util.Random].
 *
 * The custom implementation is particularly useful in testing environments, where deterministic
 * random number generation is necessary for consistent and thorough testing.
 *
 * This test utilizes [Konsist] to scan the project and verify that the standard library's Random
 * classes are not improperly imported or used.
 * > If you need to bypass the tests for specific cases, you can suppress them by using the
 * > appropriate annotations:
 * - `@Suppress("konsist.avoid usage of stdlib Random in properties")`
 * - `@Suppress("konsist.avoid usage of stdlib Random in getter properties")`
 * - `@Suppress("konsist.avoid usage of stdlib Random in setter properties")`
 * - `@Suppress("konsist.avoid usage of stdlib Random in functions")`
 * - `@Suppress("konsist.avoid usage of stdlib Random in parameters")`
 *
 * @see [org.rsmod.api.random.GameRandom]
 */
class StdRandomKonsistTest {
    @Test
    fun `avoid usage of stdlib Random imports`() {
        val imports = KonsistScope.imports
        imports.assertFalse(additionalMessage = ERROR_MESSAGE) {
            it.name == "java.util.concurrent.ThreadLocalRandom"
        }
    }

    @Test
    fun `avoid usage of stdlib Random in properties`() {
        val properties = KonsistScope.properties(includeNested = true)
        properties.assertFalse(additionalMessage = ERROR_MESSAGE) { it.isOrUsesStdRandom() }
    }

    // NOTE: Due to limitations in Konsist's ability to fully analyze getter return types and
    // receiver types, it is currently possible for getters to bypass this test by returning direct
    // Random values (e.g., `(0..1).random()`). As an alternative, we could enforce stricter checks
    // by not verifying the receiver type (e.g., IntRange or standard Collections) and instead
    // outright forbidding any use of `random()` and `return random()` in getters. However, for now,
    // I have decided against this as it can intervene with non-Random related code.

    @Test
    fun `avoid usage of stdlib Random in getter properties`() {
        val getters = KonsistScope.properties(includeNested = true).getters
        getters.variables.assertFalse(additionalMessage = ERROR_MESSAGE) { it.isOrUsesStdRandom() }
    }

    @Test
    fun `avoid usage of stdlib Random in setter properties`() {
        val setters = KonsistScope.properties(includeNested = true).setters
        setters.variables.assertFalse(additionalMessage = ERROR_MESSAGE) { it.isOrUsesStdRandom() }
    }

    @Test
    fun `avoid usage of stdlib Random in functions`() {
        val scopeFunctions = KonsistScope.functions(includeNested = true, includeLocal = true)
        val variables = scopeFunctions.variables
        val parameters = scopeFunctions.parameters.discardSuspendLambdas()
        val functions = scopeFunctions.discardSuspendReturnBlocks()
        variables.assertFalse(additionalMessage = ERROR_MESSAGE) { it.isOrUsesStdRandom() }
        functions.assertFalse(additionalMessage = ERROR_MESSAGE) { it.containsStdRandomUsage() }
        parameters.assertFalse(additionalMessage = ERROR_MESSAGE) { it.isRandomReference() }
    }

    @Test
    fun `avoid usage of stdlib Random in parameters`() {
        val functions = KonsistScope.functions(includeNested = true, includeLocal = true)
        val parameters = functions.parameters.discardSuspendLambdas()
        parameters.assertFalse(additionalMessage = ERROR_MESSAGE) { it.isRandomReference() }
    }

    private companion object {
        private const val RANDOM_GENERATION_ERROR_MESSAGE =
            "Random number generation should come from a " +
                "[org.rsmod.api.random.GameRandom] implementation."

        private val ERROR_MESSAGE =
            """
            | - $RANDOM_GENERATION_ERROR_MESSAGE
            """
                .trimMargin()
    }
}

private fun KoVariableDeclaration.isRandomReference(): Boolean =
    hasRandomType() || hasRandomTacitType()

private fun KoPropertyDeclaration.isRandomReference(): Boolean =
    hasRandomType() || hasRandomTacitType()

private fun KoParameterDeclaration.isRandomReference(): Boolean = hasRandomType()

private fun KoNullableTypeProvider.hasRandomType(): Boolean =
    hasTypeOf(Random::class) ||
        hasTypeOf(java.util.Random::class) ||
        hasTypeOf(java.util.concurrent.ThreadLocalRandom::class)

private fun KoTacitTypeProvider.hasRandomTacitType(): Boolean =
    hasTacitTypeOf(Random::class) ||
        hasTacitTypeOf(java.util.Random::class) ||
        hasTacitTypeOf(java.util.concurrent.ThreadLocalRandom::class)

private fun KoNonNullableTypeProvider.hasRandomType(): Boolean =
    hasTypeOf(Random::class) ||
        hasTypeOf(java.util.Random::class) ||
        hasTypeOf(java.util.concurrent.ThreadLocalRandom::class)

// NOTE: the following is a very naive approach to checking if declaration sites are using standard
// `random` calls. This relies on the `GameRandom` functions to _not_ be named `random`. This is
// required because even though Konsist is smart, it cannot perform high-level type inference.

private val NAIVE_RANDOM_CALL_REGEX = Regex(""".+\.random\(\)""")
private val NAIVE_RANDOM_RETURN_NO_BODY_REGEX =
    Regex("""[^\n]*=\s*(?:[A-Za-z_]\w*\.)*(?:Random|ThreadLocalRandom)\b""")
private val NAIVE_RANDOM_RETURN_WITH_BODY_REGEX =
    Regex("""\breturn\b[^\n]*\b(?:[A-Za-z_]\w*\.)*(?:Random|ThreadLocalRandom)\b""")
private val NAIVE_RANDOM_EXTENSION_RETURN_NO_BODY_REGEX = Regex(""".*= random\(\)""")
private val NAIVE_RANDOM_EXTENSION_RETURN_WITH_BODY_REGEX = Regex(""".*return random\(\)""")

private fun KoVariableDeclaration.isOrUsesStdRandom(): Boolean =
    isRandomReference() || value?.matches(NAIVE_RANDOM_CALL_REGEX) == true

private fun KoPropertyDeclaration.isOrUsesStdRandom(): Boolean {
    val isRandomReference = isRandomReference()
    if (isRandomReference) {
        return true
    }
    val callsStdRandom = value?.matches(NAIVE_RANDOM_CALL_REGEX) == true
    if (callsStdRandom) {
        return true
    }
    return text.contains(NAIVE_RANDOM_RETURN_NO_BODY_REGEX) ||
        text.contains(NAIVE_RANDOM_RETURN_WITH_BODY_REGEX)
}

private fun KoFunctionDeclaration.containsStdRandomUsage(): Boolean {
    if (text.contains(NAIVE_RANDOM_CALL_REGEX)) {
        return true
    }
    if (isValidExtensionReturningBasicType()) {
        if (extensionContainsRandomReturnStatement()) {
            return true
        }
    }
    return containsRandomReturnStatement()
}

private fun KoFunctionDeclaration.isValidExtensionReturningBasicType(): Boolean {
    // KoFunctionDeclaration may not always be able to infer the return type.
    /* https://docs.konsist.lemonappdev.com/features/compiler-type-inference */
    val returnsBasicType = !hasReturnType() || returnType?.isKotlinBasicType == true
    val isCollectionExtension = receiverType?.isKotlinCollectionType == true
    val isIntRangeExtension = receiverType?.name == "IntRange"
    val isValidExtensionReceiver = isCollectionExtension || isIntRangeExtension
    return returnsBasicType && isValidExtensionReceiver
}

private fun KoFunctionDeclaration.extensionContainsRandomReturnStatement(): Boolean =
    if (hasBlockBody) {
        text.contains(NAIVE_RANDOM_EXTENSION_RETURN_WITH_BODY_REGEX)
    } else {
        text.contains(NAIVE_RANDOM_EXTENSION_RETURN_NO_BODY_REGEX)
    }

private fun KoFunctionDeclaration.containsRandomReturnStatement(): Boolean =
    if (hasBlockBody) {
        text.contains(NAIVE_RANDOM_RETURN_WITH_BODY_REGEX)
    } else {
        text.contains(NAIVE_RANDOM_RETURN_NO_BODY_REGEX)
    }

/**
 * Filters out parameters with a suspend block that cause an internal error in Konsist.
 *
 * Due to a bug in Konsist, parameters containing a suspend lambda throw an internal error when
 * their types are being resolved. This error is not handled by the Konsist back-end, resulting in
 * incorrect test outcomes. This function filters out such parameters to prevent tests from failing
 * due to this issue, though this is a workaround rather than an ideal solution.
 *
 * @return A list of [KoParameterDeclaration] objects that do not trigger the Konsist internal
 *   error.
 */
private fun List<KoParameterDeclaration>.discardSuspendLambdas(): List<KoParameterDeclaration> {
    val valid = mutableListOf<KoParameterDeclaration>()
    for (parameter in this) {
        try {
            parameter.hasTypeOf(Unit::class) // Causes a Konsist internal error.
            valid += parameter
        } catch (_: KoInternalException) {
            // Ignore the parameter causing the error
        }
    }
    return valid
}

/**
 * Filters out functions that return a suspend block that cause an internal error in Konsist.
 *
 * Due to a bug in Konsist, return types containing a suspend lambda throw an internal error when
 * their types are being resolved. This error is not handled by the Konsist back-end, resulting in
 * incorrect test outcomes. This function filters out such parameters to prevent tests from failing
 * due to this issue, though this is a workaround rather than an ideal solution.
 *
 * @return A list of [KoFunctionDeclaration] objects that do not trigger the Konsist internal error.
 */
private fun List<KoFunctionDeclaration>.discardSuspendReturnBlocks(): List<KoFunctionDeclaration> {
    val valid = mutableListOf<KoFunctionDeclaration>()
    for (parameter in this) {
        try {
            parameter.returnType?.isKotlinBasicType // Causes a Konsist internal error.
            valid += parameter
        } catch (_: KoInternalException) {
            // Ignore the parameter causing the error
        }
    }
    return valid
}
