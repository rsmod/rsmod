package org.rsmod.api.registry.loc

public sealed class LocRegistryResult {
    public sealed class Add : LocRegistryResult()

    public object AddSpawned : Add()

    public object AddMapLoc : Add()

    public sealed class Delete : LocRegistryResult()

    public object DeleteSpawned : Delete()

    public object DeleteMapLoc : Delete()

    public object DeleteFailed : Delete()
}
