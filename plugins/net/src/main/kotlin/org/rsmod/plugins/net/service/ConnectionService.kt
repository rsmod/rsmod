package org.rsmod.plugins.net.service

public sealed class ConnectionService {
    object InitGame : ConnectionService()
    object InitJs5Remote : ConnectionService()
}
