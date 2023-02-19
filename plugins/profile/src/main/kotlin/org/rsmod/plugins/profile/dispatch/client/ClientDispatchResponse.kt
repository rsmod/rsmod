package org.rsmod.plugins.profile.dispatch.client

import org.rsmod.plugins.profile.dispatch.DispatchResponse

public sealed class ClientRegisterResponse : DispatchResponse {

    public object Success : ClientRegisterResponse()
}

public sealed class ClientDeregisterResponse : DispatchResponse {

    public object Success : ClientDeregisterResponse()
}
