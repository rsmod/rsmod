package gg.rsmod.game.action

import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionExecutorTests {

    @ParameterizedTest
    @ArgumentsSource(ActionTypeProvider::class)
    fun `register different executors on same action type`(type: ActionType) {
        val map = ActionMap()

        map.register<Nothing>(type, 0) {}
        Assertions.assertEquals(1, map.size)
        map.register<Nothing>(type, 1) {}
        Assertions.assertEquals(1, map.size)

        val executors = map[type]
        Assertions.assertNotNull(executors)
        Assertions.assertEquals(2, executors?.size)
    }

    @ParameterizedTest
    @ArgumentsSource(ActionTypePairProvider::class)
    fun `register executors on different action types`(type1: ActionType, type2: ActionType) {
        val map = ActionMap()

        map.register<Nothing>(type1, 0) {}
        Assertions.assertEquals(1, map.size)
        map.register<Nothing>(type2, 0) {}
        Assertions.assertEquals(2, map.size)
    }

    @ParameterizedTest
    @ArgumentsSource(ActionTypeProvider::class)
    fun `fail to overwrite same id executor on same action type`(type: ActionType) {
        val map = ActionMap()

        map.register<Nothing>(type, 0) {}
        Assertions.assertEquals(1, map.size)
        val registered = map.register<Nothing>(type, 0) {}
        Assertions.assertFalse(registered)
        Assertions.assertEquals(1, map.size)

        val executors = map[type]
        Assertions.assertNotNull(executors)
        Assertions.assertEquals(1, executors?.size)
    }

    @ParameterizedTest
    @ArgumentsSource(ActionTypePairProvider::class)
    fun `register same id executor on different action types`(type1: ActionType, type2: ActionType) {
        val map = ActionMap()

        map.register<Nothing>(type1, 0) {}
        Assertions.assertEquals(1, map.size)
        map.register<Nothing>(type2, 0) {}
        Assertions.assertEquals(2, map.size)

        val executors1 = map[type1]
        Assertions.assertNotNull(executors1)
        Assertions.assertEquals(1, executors1?.size)

        val executors2 = map[type1]
        Assertions.assertNotNull(executors2)
        Assertions.assertEquals(1, executors2?.size)
    }
}

private object ActionTypeProvider : ArgumentsProvider {

    private object TestAction : ActionType

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(Arguments.of(TestAction))
    }
}

private object ActionTypePairProvider : ArgumentsProvider {

    private object TestAction1 : ActionType
    private object TestAction2 : ActionType

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(Arguments.of(TestAction1, TestAction2))
    }
}
