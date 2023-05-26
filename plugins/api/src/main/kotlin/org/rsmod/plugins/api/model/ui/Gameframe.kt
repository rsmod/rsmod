package org.rsmod.plugins.api.model.ui

import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public interface Gameframe {

    public val topLevel: NamedInterface

    public val overlays: Iterable<NamedComponent>
}
