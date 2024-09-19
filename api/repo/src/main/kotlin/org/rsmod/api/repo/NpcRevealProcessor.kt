package org.rsmod.api.repo

import jakarta.inject.Inject
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.game.entity.Npc

public class NpcRevealProcessor @Inject constructor(private val npcRepo: NpcRepository) {
    public fun process(npc: Npc) {
        npcRepo.processReveal(npc)
    }
}
