package org.rsmod.plugins.net.js5.upstream

public sealed class Js5Request {

    public data class Group(val archive: Int, val group: Int, val urgent: Boolean) : Js5Request()
    public data class Rekey(val key: Int) : Js5Request()

    public object LoggedIn : Js5Request()
    public object LoggedOut : Js5Request()
    public object Connected : Js5Request()
    public object Disconnect : Js5Request()
}
