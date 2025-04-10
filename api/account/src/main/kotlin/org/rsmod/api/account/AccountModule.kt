package org.rsmod.api.account

import org.rsmod.api.account.character.CharacterModule
import org.rsmod.api.account.loader.AccountLoaderModule
import org.rsmod.api.account.saver.AccountSavingModule
import org.rsmod.module.ExtendedModule

public object AccountModule : ExtendedModule() {
    override fun bind() {
        install(CharacterModule)
        install(AccountLoaderModule)
        install(AccountSavingModule)
        bindInstance<AccountManager>()
    }
}
