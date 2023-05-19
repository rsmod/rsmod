package org.rsmod.plugins.api.model.event

import org.rsmod.plugins.api.model.ui.InterfaceType
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public object DownstreamEvent {

    public data class IfOpenTop(public val topLevel: NamedInterface) : TypePlayerKeyedEvent

    public data class IfCloseTop(public val topLevel: NamedInterface) : TypePlayerKeyedEvent

    public data class IfOpenSub(
        public val sub: NamedInterface,
        public val target: NamedComponent,
        public val type: InterfaceType
    ) : TypePlayerKeyedEvent

    public data class IfCloseSub(
        public val sub: NamedInterface,
        public val target: NamedComponent
    ) : TypePlayerKeyedEvent
}
