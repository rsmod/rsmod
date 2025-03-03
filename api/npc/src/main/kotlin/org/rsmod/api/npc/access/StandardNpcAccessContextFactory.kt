package org.rsmod.api.npc.access

import jakarta.inject.Inject
import org.rsmod.api.npc.hit.processor.StandardNpcHitProcessor
import org.rsmod.api.random.GameRandom

public class StandardNpcAccessContextFactory
@Inject
constructor(private val random: GameRandom, private val hitProcessor: StandardNpcHitProcessor) {
    public fun create(): StandardNpcAccessContext =
        StandardNpcAccessContext(getRandom = { random }, getHitProcessor = { hitProcessor })
}
