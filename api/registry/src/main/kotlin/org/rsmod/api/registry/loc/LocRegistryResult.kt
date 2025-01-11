package org.rsmod.api.registry.loc

public sealed class LocRegistryResult {
    public sealed class Add : LocRegistryResult()

    public data object AddSpawned : Add()

    public data object AddMapLoc : Add()

    public sealed class Delete : LocRegistryResult()

    public data object DeleteSpawned : Delete()

    public data object DeleteMapLoc : Delete()

    public data object DeleteFailed : Delete()
}
