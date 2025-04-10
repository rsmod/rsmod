package org.rsmod.api.account.loader.request

/**
 * A callback used to deliver the result of an account load request.
 *
 * _This function is called asynchronously on a separate thread - not the thread that initiated the
 * request. Implementations must ensure thread safety when handling [invoke]._
 */
public fun interface AccountLoadCallback {
    public operator fun invoke(response: AccountLoadResponse)
}
