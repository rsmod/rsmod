package org.rsmod.plugins.api.model.ui

public sealed class InterfaceType {

    public object Overlay : InterfaceType()
    public object Modal : InterfaceType()
}
