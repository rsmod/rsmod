package org.rsmod.game.type.obj

public enum class Wearpos(public val slot: Int) {
    Hat(0),
    Back(1),
    Front(2),
    RightHand(3),
    Torso(4),
    LeftHand(5),
    Arms(6),
    Legs(7),
    Head(8), // "Hair"
    Hands(9),
    Feet(10),
    Jaw(11),
    Ring(12),
    Quiver(13);

    public companion object {
        public fun forSlot(slot: Int): Wearpos? =
            when (slot) {
                0 -> Hat
                1 -> Back
                2 -> Front
                3 -> RightHand
                4 -> Torso
                5 -> LeftHand
                6 -> Arms
                7 -> Legs
                8 -> Head
                9 -> Hands
                10 -> Feet
                11 -> Jaw
                12 -> Ring
                13 -> Quiver
                else -> null
            }
    }
}
