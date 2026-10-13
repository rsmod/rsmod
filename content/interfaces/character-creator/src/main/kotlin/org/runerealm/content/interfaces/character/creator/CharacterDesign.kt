package org.runerealm.content.interfaces.character.creator

import org.runerealm.content.interfaces.character.creator.configs.creator_components
import org.runerealm.game.type.comp.ComponentType

/** Number of identity kit slots per body type. Body type B kits use `bodyPart + 7`. */
const val BODY_PARTS_PER_TYPE: Int = 7

const val BODY_TYPE_A: Int = 0
const val BODY_TYPE_B: Int = 1

const val PRONOUN_HE_HIM: Int = 0
const val PRONOUN_SHE_HER: Int = 1
const val PRONOUN_THEY_THEM: Int = 2

/** `tutorial` varp value once the player has finished creating their character. */
const val TUTORIAL_COMPLETE: Int = 1000

/** A body part row on the left side of the Character Creator (`Head`, `Jaw`, ...). */
enum class DesignBodyPart(val part: Int, val left: ComponentType, val right: ComponentType) {
    Head(0, creator_components.head_left, creator_components.head_right),
    Jaw(1, creator_components.jaw_left, creator_components.jaw_right),
    Torso(2, creator_components.torso_left, creator_components.torso_right),
    Arms(3, creator_components.arms_left, creator_components.arms_right),
    Hands(4, creator_components.hands_left, creator_components.hands_right),
    Legs(5, creator_components.legs_left, creator_components.legs_right),
    Feet(6, creator_components.feet_left, creator_components.feet_right),
}

/**
 * A colour row on the right side of the Character Creator. [slot] is the appearance colour slot and
 * [count] the number of colours offered, per the OSRS Wiki:
 * - Hair: 30 colours ([Hairstyles#Colours](https://oldschool.runescape.wiki/w/Hairstyles#Colours)).
 * - Torso/Legs: 29 clothing colours ([Makeover#Clothing
 *   colours](https://oldschool.runescape.wiki/w/Makeover#Clothing_colours)).
 * - Feet: 6 shoe colours ([Makeover#Shoes](https://oldschool.runescape.wiki/w/Makeover#Shoes)).
 * - Skin: the 8 standard skin colours. The remaining 6 of the wiki's 14 skin colours are unlocks
 *   (Halloween events, demonic skin contract) and are not offered on character creation.
 */
enum class DesignColour(
    val slot: Int,
    val count: Int,
    val left: ComponentType,
    val right: ComponentType,
) {
    Hair(0, 30, creator_components.hair_left, creator_components.hair_right),
    Torso(1, 29, creator_components.torso_col_left, creator_components.torso_col_right),
    Legs(2, 29, creator_components.legs_col_left, creator_components.legs_col_right),
    Feet(3, 6, creator_components.feet_col_left, creator_components.feet_col_right),
    Skin(4, 8, creator_components.skin_left, creator_components.skin_right),
}
