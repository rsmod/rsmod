package org.rsmod.plugins.net.js5.upstream

sealed class Js5Request {

    data class Group(val archive: Int, val group: Int, val urgent: Boolean) : Js5Request()
    data class Rekey(val key: Int) : Js5Request()

    object LoggedIn : Js5Request()
    object LoggedOut : Js5Request()
    object Connected : Js5Request()
    object Disconnect : Js5Request()
}
