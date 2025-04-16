package org.rsmod.server.shared.module

import com.google.inject.Provider
import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEventMap
import org.rsmod.events.SuspendEventMap
import org.rsmod.events.UnboundEventMap
import org.rsmod.module.ExtendedModule

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
