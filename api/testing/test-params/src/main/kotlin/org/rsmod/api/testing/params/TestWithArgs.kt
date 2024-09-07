package org.rsmod.api.testing.params

import kotlin.reflect.KClass
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

/**
 * [TestWithArgs] is an annotation that can be used to register a game test with multiple arguments.
 *
 * If you wish to use this functionality, your `GameTestState` parameter must be placed _last_ on
 * the args list, _after_ your provided [TestArgs].
 *
 * ### Example:
 * ```
 * fun GameTestState.`your conventional test method`() = runGameTest { ... }
 * ```
 *
 * *Would become*:
 * ```
 * @TestWithArgs(YourTestArgsProvider::class)
 * fun `your new test method`(testArg1, ..., state: GameTestState) = state.runGameTest { ... }
 * ```
 */
@ParameterizedTest
@ArgumentsSource(ArgumentsProviderDelegate::class)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class TestWithArgs(val provider: KClass<out TestArgsProvider>)
