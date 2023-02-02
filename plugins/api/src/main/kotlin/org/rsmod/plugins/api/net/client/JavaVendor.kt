package org.rsmod.plugins.api.net.client

public sealed class JavaVendor {

    public object Sun : JavaVendor()
    public object Microsoft : JavaVendor()
    public object Apple : JavaVendor()
    public object Other : JavaVendor()
    public object Oracle : JavaVendor()
}
