package org.rsmod.api.player.input

import org.rsmod.game.type.comp.ComponentType

public data class ResumePauseButtonInput(
    public val component: ComponentType,
    public val subcomponent: Int,
) {
    public fun isComponentType(type: ComponentType): Boolean = type.isType(component)

    public fun isSubcomponent(comsub: Int): Boolean = subcomponent == comsub
}
