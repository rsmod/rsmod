package org.rsmod.api.testing.params

import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.support.ParameterDeclarations
import org.junit.platform.commons.util.ReflectionUtils

public class ArgumentsProviderDelegate : ArgumentsProvider {
    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext,
    ): Stream<out Arguments> {
        val testMethod = context.requiredTestMethod
        val testWithArgs = testMethod.getAnnotation(TestWithArgs::class.java)
        val providerClass = testWithArgs.provider.java
        val providerInstance = ReflectionUtils.newInstance(providerClass)
        val arguments = providerInstance.args().map { Arguments.of(it.first, *it.rest) }
        return Stream.of(*arguments.toTypedArray())
    }
}
