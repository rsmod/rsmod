package org.rsmod.api.account

import jakarta.inject.Inject
import org.rsmod.api.account.loader.AccountLoaderService
import org.rsmod.api.account.loader.request.AccountLoadCallback
import org.rsmod.api.account.loader.request.AccountLoadRequest
import org.rsmod.api.account.saver.AccountSavingService
import org.rsmod.api.account.saver.request.AccountSaveCallback
import org.rsmod.api.account.saver.request.AccountSaveRequest
import org.rsmod.game.entity.Player

public class AccountManager
@Inject
constructor(private val loader: AccountLoaderService, private val saver: AccountSavingService) {
    public fun save(characterId: Int, player: Player, callback: AccountSaveCallback) {
        val saveRequest = AccountSaveRequest(characterId, player, callback)
        saver.queue(saveRequest)
    }

    public fun load(loginName: String, callback: AccountLoadCallback): Boolean {
        val loadRequest = AccountLoadRequest.StrictSearch(loginName, callback)
        return loader.queue(loadRequest)
    }

    public fun loadOrCreate(
        loginName: String,
        hashedPassword: () -> String,
        callback: AccountLoadCallback,
    ): Boolean {
        val loadRequest =
            AccountLoadRequest.SearchOrCreateWithPassword(hashedPassword, loginName, callback)
        return loader.queue(loadRequest)
    }

    public fun isLoaderShuttingDown(): Boolean = loader.isShuttingDown()

    public fun isLoaderRejectingRequests(): Boolean = loader.isTemporarilyRejectingRequests()
}
