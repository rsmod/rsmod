package org.rsmod.api.repo

import jakarta.inject.Inject
import org.rsmod.api.repo.controller.ControllerRepository
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.repo.obj.ObjRepository

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
