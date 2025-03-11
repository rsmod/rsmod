package org.rsmod.api.npc.access

import jakarta.inject.Inject
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.processor.NpcHitProcessor
import org.rsmod.api.random.GameRandom

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
