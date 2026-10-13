package org.runerealm.api.account.character

import org.runerealm.api.account.character.inv.CharacterInventoryApplier
import org.runerealm.api.account.character.inv.CharacterInventoryPipeline
import org.runerealm.api.account.character.main.CharacterAccountApplier
import org.runerealm.api.account.character.main.CharacterAccountRepository
import org.runerealm.api.account.character.stats.CharacterStatApplier
import org.runerealm.api.account.character.stats.CharacterStatPipeline
import org.runerealm.module.ExtendedModule

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
