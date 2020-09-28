package gg.rsmod.game.model.model

import gg.rsmod.game.model.mob.Mob
import gg.rsmod.game.model.mob.NpcList
import gg.rsmod.game.model.mob.PlayerList
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MobListTests {

    @ParameterizedTest
    @ArgumentsSource(MobListProvider::class)
    fun `size() only counts non-null elements`(mobList: List<Mob>) {
        Assertions.assertNotEquals(0, mobList.indices.last)
        Assertions.assertEquals(0, mobList.size)
        Assertions.assertTrue(mobList.isEmpty())
    }
}

private object MobListProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(PlayerList()),
            Arguments.of(NpcList())
        )
    }
}
