package org.rsmod.api.testing.params

public data class TestArgsList(val argsList: List<TestArgs>) : List<TestArgs> by argsList

/**
 * Creates a [TestArgsList] containing [TestArgs] for tests with a single parameter, excluding the
 * `GameTestState` parameter.
 *
 * @param args The arguments to be converted into a list of [TestArgs].
 * @return A [TestArgsList] containing the provided arguments, each wrapped in [TestArgs].
 * @see [TestWithArgs]
 */
public fun <T : Any?> testArgsOfSingleParam(vararg args: T): TestArgsList =
    TestArgsList(args.map { TestArgs(it) })
