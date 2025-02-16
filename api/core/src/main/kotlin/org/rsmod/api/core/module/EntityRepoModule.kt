package org.rsmod.api.core.module

import org.rsmod.api.repo.EntityLifecycleProcess
import org.rsmod.api.repo.controller.ControllerRepository
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.repo.player.PlayerRepository
import org.rsmod.api.repo.region.RegionRepository
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.module.ExtendedModule

public object EntityRepoModule : ExtendedModule() {
    override fun bind() {
        bindInstance<EntityLifecycleProcess>()
        bindInstance<LocRepository>()
        bindInstance<NpcRepository>()
        bindInstance<ObjRepository>()
        bindInstance<PlayerRepository>()
        bindInstance<ControllerRepository>()
        bindInstance<RegionRepository>()
        bindInstance<WorldRepository>()
    }
}
