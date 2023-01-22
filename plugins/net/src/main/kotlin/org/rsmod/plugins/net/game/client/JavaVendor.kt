package org.rsmod.plugins.net.game.client

sealed class JavaVendor {

    object Sun : JavaVendor()
    object Microsoft : JavaVendor()
    object Apple : JavaVendor()
    object Other : JavaVendor()
    object Oracle : JavaVendor()
}
