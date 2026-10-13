package org.runerealm.api.repo

import jakarta.inject.Inject
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.game.entity.Npc

public class NpcRevealProcessor @Inject constructor(private val npcRepo: NpcRepository) {
    public fun process(npc: Npc) {
        npcRepo.processReveal(npc)
    }
}
