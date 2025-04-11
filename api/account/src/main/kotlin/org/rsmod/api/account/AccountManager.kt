package org.rsmod.api.account

import jakarta.inject.Inject
import org.rsmod.api.account.loader.AccountLoaderService
import org.rsmod.api.account.loader.request.AccountLoadAuth
import org.rsmod.api.account.loader.request.AccountLoadCallback
import org.rsmod.api.account.loader.request.AccountLoadRequest
import org.rsmod.api.account.saver.AccountSavingService
import org.rsmod.api.account.saver.request.AccountSaveCallback
import org.rsmod.api.account.saver.request.AccountSaveRequest
import org.rsmod.game.entity.Player

public class AccountManager
@Inject
constructor(private val loader: AccountLoaderService, private val saver: AccountSavingService) {
    public fun save(player: Player, callback: AccountSaveCallback) {
        val request = AccountSaveRequest(player.accountId, player.characterId, player, callback)
        saver.queue(request)
    }

    public fun load(
        auth: AccountLoadAuth,
        loginName: String,
        callback: AccountLoadCallback,
    ): Boolean {
        val loadRequest = AccountLoadRequest.StrictSearch(auth, loginName, callback)
        return loader.queue(loadRequest)
    }

    public fun loadOrCreate(
        auth: AccountLoadAuth,
        loginName: String,
        hashedPassword: () -> String,
        callback: AccountLoadCallback,
    ): Boolean {
        val loadRequest =
            AccountLoadRequest.SearchOrCreateWithPassword(hashedPassword, auth, loginName, callback)
        return loader.queue(loadRequest)
    }

    public fun isLoaderShuttingDown(): Boolean = loader.isShuttingDown()

    public fun isLoaderRejectingRequests(): Boolean = loader.isTemporarilyRejectingRequests()
}
