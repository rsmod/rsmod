package org.rsmod.api.account.loader.request

public sealed class AccountLoadRequest {
    public abstract val auth: AccountLoadAuth
    public abstract val loginName: String
    public abstract val callback: AccountLoadCallback

    /**
     * Account requests that only require read access to the database or other account-related
     * storage services.
     *
     * Properly labeling requests as read or write enables systems to optimize operations based on
     * access type. For example, read-only requests can be executed in parallel - even in systems
     * like sqlite, which benefit from concurrent reads but not concurrent writes.
     */
    public sealed class ReadOnly : AccountLoadRequest()

    public data class StrictSearch(
        override val auth: AccountLoadAuth,
        override val loginName: String,
        override val callback: AccountLoadCallback,
    ) : ReadOnly()

    /**
     * Account requests that require write access to the database or other account-related storage
     * services.
     *
     * Properly labeling requests as read or write enables systems to optimize operations based on
     * access type. For example, read-only requests can be executed in parallel - even in systems
     * like sqlite, which benefit from concurrent reads but not concurrent writes.
     */
    public sealed class WriteRequired : AccountLoadRequest()

    public data class SearchOrCreateWithPassword(
        public val hashedPassword: () -> String,
        override val auth: AccountLoadAuth,
        override val loginName: String,
        override val callback: AccountLoadCallback,
    ) : WriteRequired()
}
