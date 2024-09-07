package org.rsmod.api.route

import org.rsmod.module.ExtendedModule

public object RouteModule : ExtendedModule() {
    override fun bind() {
        bindInstance<BoundValidator>()
        bindInstance<RayCastValidator>()
        bindInstance<RayCastFactory>()
        bindInstance<RouteFactory>()
        bindInstance<StepFactory>()
    }
}
