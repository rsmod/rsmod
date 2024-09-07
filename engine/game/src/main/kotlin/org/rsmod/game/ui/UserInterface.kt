package org.rsmod.game.ui

@JvmInline
public value class UserInterface(public val id: Int) {
    public companion object {
        public val NULL: UserInterface = UserInterface(-1)
    }
}
