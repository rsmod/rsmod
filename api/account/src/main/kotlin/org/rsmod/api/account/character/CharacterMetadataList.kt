package org.rsmod.api.account.character

import org.rsmod.api.account.character.main.CharacterAccountData

@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
public data class CharacterMetadataList(
    val accountData: CharacterAccountData,
    val transformers: MutableList<CharacterDataTransformer<*>>,
) : List<CharacterDataTransformer<*>> by transformers {
    public val characterId: Int
        get() = accountData.characterId

    public fun <T : CharacterDataStage.Segment> add(
        applier: CharacterDataStage.Applier<T>,
        segment: T,
    ) {
        val transformer = CharacterDataTransformer(applier, segment)
        transformers.add(transformer)
    }
}
