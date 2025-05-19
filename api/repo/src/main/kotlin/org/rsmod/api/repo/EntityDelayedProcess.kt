package org.rsmod.api.repo

import jakarta.inject.Inject
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.repo.obj.ObjRepository

public class EntityDelayedProcess
@Inject
constructor(private val npcRepo: NpcRepository, private val objRepo: ObjRepository) {
    public fun process() {
        npcRepo.processDelayedAdd()
        objRepo.processDelayedAdd()
    }
}
