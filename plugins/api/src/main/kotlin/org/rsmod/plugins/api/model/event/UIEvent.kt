package org.rsmod.plugins.api.model.event

import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public sealed class UIEvent {

    public data class OpenTopLevel(
        val topLevel: NamedInterface
    ) : TypePlayerKeyedEvent

    public data class OpenOverlay(
        val overlay: NamedInterface,
        val target: NamedComponent
    ) : TypePlayerKeyedEvent
}
