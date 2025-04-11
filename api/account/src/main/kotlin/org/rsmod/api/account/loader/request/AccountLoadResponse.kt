package org.rsmod.api.account.loader.request

import org.rsmod.api.account.character.CharacterDataTransformer
import org.rsmod.api.account.character.main.CharacterAccountData

public sealed class AccountLoadResponse {
    public sealed class Ok : AccountLoadResponse() {
        public abstract val auth: AccountLoadAuth
        public abstract val account: CharacterAccountData
        public abstract val transforms: List<CharacterDataTransformer<*>>

        public data class NewAccount(
            override val auth: AccountLoadAuth,
            override val account: CharacterAccountData,
            override val transforms: List<CharacterDataTransformer<*>>,
        ) : Ok()

        public data class LoadAccount(
            override val auth: AccountLoadAuth,
            override val account: CharacterAccountData,
            override val transforms: List<CharacterDataTransformer<*>>,
        ) : Ok()
    }

    public sealed class Err : AccountLoadResponse() {
        public data object AccountNotFound : Err()

        public data object ShutdownInProgress : Err()

        public data object InternalServiceError : Err()

        public data object Timeout : Err()

        public data class Exception(val reason: Throwable) : Err()
    }
}
