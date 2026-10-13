package org.runerealm.api.npc.access

import jakarta.inject.Inject
import org.runerealm.api.npc.hit.modifier.NpcHitModifier
import org.runerealm.api.npc.hit.processor.NpcHitProcessor
import org.runerealm.api.random.GameRandom

public class StandardNpcAccessContextFactory
@Inject
constructor(
    private val random: GameRandom,
    private val hitModifier: NpcHitModifier,
    private val hitProcessor: NpcHitProcessor,
) {
    public fun create(): StandardNpcAccessContext =
        StandardNpcAccessContext(
            getRandom = { random },
            getHitModifier = { hitModifier },
            getHitProcessor = { hitProcessor },
        )
}
