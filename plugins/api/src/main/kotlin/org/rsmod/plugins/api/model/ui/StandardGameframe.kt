package org.rsmod.plugins.api.model.ui

import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedInterface

public interface StandardGameframe : Gameframe {

    public val topLevel: NamedInterface

    public val overlays: Iterable<NamedComponent>
}
