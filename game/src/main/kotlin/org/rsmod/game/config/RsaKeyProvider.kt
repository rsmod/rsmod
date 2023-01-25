package org.rsmod.game.config

import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters
import org.openrs2.crypto.Rsa
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Provider

public class RsaKeyProvider @Inject constructor(
    private val config: GameConfig
) : Provider<RSAPrivateCrtKeyParameters> {

    private val path: Path
        get() = config.dataPath.resolve("rsa/game.key")

    override fun get(): RSAPrivateCrtKeyParameters = if (Files.exists(path)) {
        Rsa.readPrivateKey(path)
    } else {
        val (_, private) = Rsa.generateKeyPair(Rsa.CLIENT_KEY_LENGTH)
        Rsa.writePrivateKey(path, private)
        private
    }
}
