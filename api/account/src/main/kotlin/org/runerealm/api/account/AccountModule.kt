package org.runerealm.api.account

import org.runerealm.api.account.character.CharacterModule
import org.runerealm.api.account.loader.AccountLoaderModule
import org.runerealm.api.account.saver.AccountSavingModule
import org.runerealm.module.ExtendedModule

public object AccountModule : ExtendedModule() {
    override fun bind() {
        install(CharacterModule)
        install(AccountLoaderModule)
        install(AccountSavingModule)
        bindInstance<AccountManager>()
    }
}
