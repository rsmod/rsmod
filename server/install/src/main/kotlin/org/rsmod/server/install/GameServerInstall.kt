package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main

fun main(args: Array<String>): Unit = GameServerInstall().main(args)

class GameServerInstall : CliktCommand(name = "install") {
    override fun run() {
        GameServerLogbackCopy().main(emptyArray())
        GameServerCacheDownloader().main(emptyArray())
        GameServerCachePacker().main(emptyArray())
        GameNetworkRsaGenerator().main(emptyArray())
    }
}
