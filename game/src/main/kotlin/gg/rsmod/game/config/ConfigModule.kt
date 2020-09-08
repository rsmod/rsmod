package gg.rsmod.game.config

import com.google.inject.Inject
import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec

class ConfigModule : KotlinModule() {

    @Provides
    fun provideGameConfig() = GameConfig(
        dataPath = Paths.get(".", "app", "data"),
        revision = 186,
        port = 43594
    )

    @Provides
    @Inject
    fun provideRsaConfig(config: GameConfig) = createRsa(config)

    private fun createRsa(config: GameConfig): RsaConfig {
        val path = config.rsaPath
        PemReader(Files.newBufferedReader(path)).use { reader ->
            val pem = reader.readPemObject()
            val keySpec = PKCS8EncodedKeySpec(pem.content)

            Security.addProvider(BouncyCastleProvider())
            val factory = KeyFactory.getInstance("RSA", "BC")

            val privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey
            val exponent = privateKey.privateExponent
            val modulus = privateKey.modulus
            return RsaConfig(exponent, modulus)
        }
    }
}
