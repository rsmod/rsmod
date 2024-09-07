package org.rsmod.server.shared.module

import com.google.inject.Provider
import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEventBus
import org.rsmod.events.SuspendEventBus
import org.rsmod.events.UnboundEventBus
import org.rsmod.module.ExtendedModule

object EventModule : ExtendedModule() {
    override fun bind() {
        bindProvider(EventBusProvider::class.java)
    }
}

private class EventBusProvider
@Inject
constructor(
    private val unbound: UnboundEventBus,
    private val keyed: KeyedEventBus,
    private val suspend: SuspendEventBus,
) : Provider<EventBus> {
    override fun get(): EventBus = EventBus(unbound, keyed, suspend)
}
