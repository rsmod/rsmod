package org.runerealm.api.repo

import jakarta.inject.Inject
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.api.repo.obj.ObjRepository

public class EntityDelayedProcess
@Inject
constructor(private val npcRepo: NpcRepository, private val objRepo: ObjRepository) {
    public fun process() {
        npcRepo.processDelayedAdd()
        objRepo.processDelayedAdd()
    }
}
