package org.rsmod.plugins.profile.dispatch.player

import org.rsmod.plugins.profile.dispatch.DispatchResponse

public sealed class PlayerRegisterResponse : DispatchResponse {

    public object Success : PlayerRegisterResponse()
    public object NoAvailableIndex : PlayerRegisterResponse()
    public object AlreadyOnline : PlayerRegisterResponse()
}

public sealed class PlayerDeregisterResponse : DispatchResponse {

    public object Success : PlayerDeregisterResponse()
    public object NotPreviouslyRegistered : PlayerDeregisterResponse()
    public object PlayerListElementMismatch : PlayerDeregisterResponse()
}
