package org.runerealm.api.repo

import jakarta.inject.Inject
import org.runerealm.api.repo.controller.ControllerRepository
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.api.repo.obj.ObjRepository

public class EntityLifecycleProcess
@Inject
constructor(
    private val npcRepo: NpcRepository,
    private val locRepo: LocRepository,
    private val objRepo: ObjRepository,
    private val conRepo: ControllerRepository,
) {
    public fun process() {
        npcRepo.processDurations()
        locRepo.processDurations()
        objRepo.processDurations()
        conRepo.processDurations()
    }
}
