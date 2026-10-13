package org.runerealm.game.type.idk

/**
 * An identity kit ("idk") is a selectable body part design used for player appearances.
 *
 * Only the properties required by the server are kept; models and recolours are client-side.
 *
 * @param bodyPart the body part slot. Slots `0..6` are used by body type `A`, while slots `7..13`
 *   are the equivalent slots for body type `B`.
 * @param disabled `true` if the kit cannot be selected by players (e.g., in the character
 *   designer).
 */
public data class IdentKitType(val id: Int, val bodyPart: Int, val disabled: Boolean) {
    public val selectable: Boolean
        get() = !disabled && bodyPart >= 0
}
