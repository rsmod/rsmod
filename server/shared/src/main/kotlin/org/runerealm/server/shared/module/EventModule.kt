package org.runerealm.server.shared.module

import com.google.inject.Provider
import jakarta.inject.Inject
import org.runerealm.events.EventBus
import org.runerealm.events.KeyedEventMap
import org.runerealm.events.SuspendEventMap
import org.runerealm.events.UnboundEventMap
import org.runerealm.module.ExtendedModule

object EventModule : ExtendedModule() {
    override fun bind() {
        bindProvider(EventBusProvider::class.java)
    }
}

private class EventBusProvider
@Inject
constructor(
    private val unbound: UnboundEventMap,
    private val keyed: KeyedEventMap,
    private val suspend: SuspendEventMap,
) : Provider<EventBus> {
    override fun get(): EventBus = EventBus(unbound, keyed, suspend)
}
