package org.rsmod.plugins.api.model.event

public sealed class PlayerSession : TypePlayerEvent {

    public object Initialize : PlayerSession()
    public object LogIn : PlayerSession()
    public object LogOut : PlayerSession()
    public object Finalize : PlayerSession()
}
