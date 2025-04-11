package org.rsmod.api.core.module

import org.rsmod.api.registry.account.AccountRegistry
import org.rsmod.api.registry.controller.ControllerRegistry
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.loc.LocRegistryNormal
import org.rsmod.api.registry.loc.LocRegistryRegion
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.api.repo.EntityLifecycleProcess
import org.rsmod.api.repo.controller.ControllerRepository
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.npc.NpcRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.repo.player.PlayerRepository
import org.rsmod.api.repo.region.RegionRepository
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.module.ExtendedModule

public object RegistryModule : ExtendedModule() {
    override fun bind() {
        bindInstance<AccountRegistry>()

        bindInstance<ControllerRegistry>()
        bindInstance<ControllerRepository>()

        bindInstance<LocRegistry>()
        bindInstance<LocRegistryNormal>()
        bindInstance<LocRegistryRegion>()
        bindInstance<LocRepository>()

        bindInstance<NpcRegistry>()
        bindInstance<NpcRepository>()

        bindInstance<ObjRegistry>()
        bindInstance<ObjRepository>()

        bindInstance<PlayerRegistry>()
        bindInstance<PlayerRepository>()

        bindInstance<RegionRegistry>()
        bindInstance<RegionRepository>()

        bindInstance<WorldRepository>()
        bindInstance<EntityLifecycleProcess>()
    }
}
