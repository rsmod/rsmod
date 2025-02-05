package org.rsmod.game.ui

import org.rsmod.game.type.interf.InterfaceType

@JvmInline
public value class UserInterface(public val id: Int) {
    public constructor(type: InterfaceType) : this(type.id)

    public companion object {
        public val NULL: UserInterface = UserInterface(-1)
    }
}
