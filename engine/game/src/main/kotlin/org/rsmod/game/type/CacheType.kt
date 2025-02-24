package org.rsmod.game.type

public abstract class CacheType {
    internal abstract var internalId: Int?

    public abstract var internalName: String?
        internal set

    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameValue: String
        get() = internalName ?: error("`internalName` must not be null.")
}

public interface HashedCacheType {
    public var startHash: Long?

    public val supposedHash: Long?
        get() = startHash
}
