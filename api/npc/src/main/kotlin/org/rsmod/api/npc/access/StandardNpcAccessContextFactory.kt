package org.rsmod.api.npc.access

import jakarta.inject.Inject
import org.rsmod.api.random.GameRandom

public class StandardNpcAccessContextFactory @Inject constructor(private val random: GameRandom) {
    public fun create(): StandardNpcAccessContext = StandardNpcAccessContext(getRandom = { random })
}
