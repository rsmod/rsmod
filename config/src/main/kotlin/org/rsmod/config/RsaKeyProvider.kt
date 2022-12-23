package org.rsmod.config

import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.openrs2.crypto.Rsa
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Provider

public class RsaKeyProvider : Provider<RSAPrivateCrtKeyParameters> {

    override fun get(): RSAPrivateCrtKeyParameters = if (Files.exists(PATH)) {
        Rsa.readPrivateKey(PATH)
    } else {
        val (_, private) = Rsa.generateKeyPair(Rsa.CLIENT_KEY_LENGTH)
        Rsa.writePrivateKey(PATH, private)
        private
    }

    private companion object {

        // TODO: configurable path
        private val PATH = Path.of(".data/rsa/game.key")
    }
}
