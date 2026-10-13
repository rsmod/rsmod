package org.runerealm.api.route

import org.runerealm.module.ExtendedModule

public object RouteModule : ExtendedModule() {
    override fun bind() {
        bindInstance<BoundValidator>()
        bindInstance<RayCastValidator>()
        bindInstance<RayCastFactory>()
        bindInstance<RouteFactory>()
        bindInstance<StepFactory>()
    }
}
