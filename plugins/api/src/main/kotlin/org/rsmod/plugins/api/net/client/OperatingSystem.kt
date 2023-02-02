package org.rsmod.plugins.api.net.client

public sealed class OperatingSystem {

    public object Windows : OperatingSystem()
    public object Mac : OperatingSystem()
    public object Linux : OperatingSystem()
    public object Other : OperatingSystem()
}
