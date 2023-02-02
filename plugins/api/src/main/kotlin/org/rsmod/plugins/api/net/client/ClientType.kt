package org.rsmod.plugins.api.net.client

public sealed class ClientType {

    public object Live : ClientType()
    public object BuildLive : ClientType()
    public object RC : ClientType()
    public object WIP : ClientType()
}
