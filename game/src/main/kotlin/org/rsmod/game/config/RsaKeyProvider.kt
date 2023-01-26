package org.rsmod.game.config

import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.openrs2.crypto.Rsa
import java.nio.file.Files
import javax.inject.Inject
import javax.inject.Provider

public class RsaKeyProvider @Inject constructor(
    private val config: GameConfig
) : Provider<RSAPrivateCrtKeyParameters> {

    override fun get(): RSAPrivateCrtKeyParameters = if (Files.exists(config.rsaPath)) {
        Rsa.readPrivateKey(config.rsaPath)
    } else {
        val (_, private) = Rsa.generateKeyPair(Rsa.CLIENT_KEY_LENGTH)
        Rsa.writePrivateKey(config.rsaPath, private)
        private
    }
}
