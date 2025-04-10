package org.rsmod.api.account.character

import org.rsmod.api.account.character.inv.CharacterInventoryApplier
import org.rsmod.api.account.character.inv.CharacterInventoryPipeline
import org.rsmod.api.account.character.main.CharacterAccountApplier
import org.rsmod.api.account.character.main.CharacterAccountRepository
import org.rsmod.api.account.character.stats.CharacterStatApplier
import org.rsmod.api.account.character.stats.CharacterStatPipeline
import org.rsmod.module.ExtendedModule

public object CharacterModule : ExtendedModule() {
    override fun bind() {
        bindInstance<CharacterAccountRepository>()

        bindInstance<CharacterAccountApplier>()
        bindInstance<CharacterInventoryApplier>()
        bindInstance<CharacterStatApplier>()

        newSetBinding<CharacterDataStage.Pipeline>()
        addSetBinding<CharacterDataStage.Pipeline>(CharacterInventoryPipeline::class.java)
        addSetBinding<CharacterDataStage.Pipeline>(CharacterStatPipeline::class.java)
    }
}
