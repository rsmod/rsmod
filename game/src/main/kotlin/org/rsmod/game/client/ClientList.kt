package org.rsmod.game.client

// TODO: could map to player index as a valid player session
// is required for a client
public class ClientList(
    private val clients: MutableList<Client> = mutableListOf()
) : MutableList<Client> by clients
