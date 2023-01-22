package org.rsmod.plugins.net.game.client

sealed class ClientType {

    object Live : ClientType()
    object BuildLive : ClientType()
    object RC : ClientType()
    object WIP : ClientType()
}
