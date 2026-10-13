package org.runerealm.api.core.module

import org.runerealm.api.registry.account.AccountRegistry
import org.runerealm.api.registry.controller.ControllerRegistry
import org.runerealm.api.registry.loc.LocRegistry
import org.runerealm.api.registry.loc.LocRegistryNormal
import org.runerealm.api.registry.loc.LocRegistryRegion
import org.runerealm.api.registry.npc.NpcRegistry
import org.runerealm.api.registry.obj.ObjRegistry
import org.runerealm.api.registry.player.PlayerRegistry
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.api.repo.EntityDelayedProcess
import org.runerealm.api.repo.EntityLifecycleProcess
import org.runerealm.api.repo.controller.ControllerRepository
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.repo.player.PlayerRepository
import org.runerealm.api.repo.region.RegionRepository
import org.runerealm.api.repo.world.WorldRepository
import org.runerealm.module.ExtendedModule

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
        bindInstance<EntityDelayedProcess>()
        bindInstance<EntityLifecycleProcess>()
    }
}
