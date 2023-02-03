package org.rsmod.game.types

@JvmInline
public value class NamedInterface(public val id: Int) {

    @Suppress("NOTHING_TO_INLINE")
    public inline fun child(child: Int): NamedComponent {
        return NamedComponent(id, child)
    }
}
