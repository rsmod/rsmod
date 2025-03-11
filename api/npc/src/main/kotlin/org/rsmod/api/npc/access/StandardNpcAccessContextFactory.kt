package org.rsmod.api.npc.access

import jakarta.inject.Inject
import org.rsmod.api.npc.hit.modifier.HitModifierNpc
import org.rsmod.api.npc.hit.processor.QueuedNpcHitProcessor
import org.rsmod.api.random.GameRandom

public class StandardNpcAccessContextFactory
@Inject
constructor(
    private val random: GameRandom,
    private val hitModifier: HitModifierNpc,
    private val hitProcessor: QueuedNpcHitProcessor,
) {
    public fun create(): StandardNpcAccessContext =
        StandardNpcAccessContext(
            getRandom = { random },
            getHitModifier = { hitModifier },
            getHitProcessor = { hitProcessor },
        )
}
