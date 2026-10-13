package org.runerealm.api.account.character.main

/**
 * Persisted player appearance. Stored as json in the `characters.appearance` column; `null` for
 * characters that have never been saved with an appearance.
 */
public data class CharacterAppearanceData(
    val bodyType: Int,
    val pronoun: Int,
    val identKit: List<Int>,
    val colours: List<Int>,
)
