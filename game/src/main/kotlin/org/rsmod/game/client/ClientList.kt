package org.rsmod.game.client

public class ClientList(
    private val clients: MutableList<Client> = mutableListOf()
) : MutableList<Client> by clients
