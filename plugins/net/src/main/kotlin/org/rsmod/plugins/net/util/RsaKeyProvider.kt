package org.rsmod.plugins.net.util

import jakarta.inject.Inject
import jakarta.inject.Provider
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.openrs2.crypto.Rsa
import org.rsmod.game.config.GameConfig
import org.rsmod.plugins.net.rsaFile
import org.rsmod.plugins.net.rsaPath
import java.nio.file.Files

public class RsaKeyProvider @Inject constructor(
    private val config: GameConfig
) : Provider<RSAPrivateCrtKeyParameters> {

    override fun get(): RSAPrivateCrtKeyParameters = if (Files.exists(config.rsaFile)) {
        Rsa.readPrivateKey(config.rsaFile)
    } else {
        Files.createDirectories(config.rsaPath)
        val (_, private) = Rsa.generateKeyPair(Rsa.CLIENT_KEY_LENGTH)
        Rsa.writePrivateKey(config.rsaFile, private)
        private
    }
}
