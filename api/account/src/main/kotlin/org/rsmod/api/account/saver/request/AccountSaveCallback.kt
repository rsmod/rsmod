package org.rsmod.api.account.saver.request

/**
 * A callback used to deliver the result of an account save request.
 *
 * _This function is called asynchronously on a separate thread - not the thread that initiated the
 * request. Implementations must ensure thread safety if required._
 */
public fun interface AccountSaveCallback {
    public operator fun invoke(response: AccountSaveResponse)
}
