package gg.rsmod.game.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.util.ConfigMap
import gg.rsmod.util.ObjectMapperProvider
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec

class ConfigModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<ObjectMapper>()
            .toProvider<ObjectMapperProvider>()
            .`in`(scope)

        bind<GameConfig>()
            .toProvider<GameConfigProvider>()
            .`in`(scope)

        bind<RsaConfig>()
            .toProvider<RsaConfigProvider>()
            .`in`(scope)
    }
}

class GameConfigProvider @Inject constructor(
    private val mapper: ObjectMapper
) : Provider<GameConfig> {

    override fun get(): GameConfig {
        val config = ConfigMap(mapper).load(CONFIG_PATH)
        val dataPath: Path = config.dataPath()
        val revision: Int = config["revision"] ?: error("Game config revision required.")
        val port: Int = config["port"] ?: DEFAULT_PORT
        return GameConfig(
            revision = revision,
            port = port,
            dataPath = dataPath
        )
    }

    private fun ConfigMap.dataPath(): Path {
        val path: String = this["data-path"] ?: return DEFAULT_DATA_PATH
        return Paths.get(path)
    }

    companion object {
        private val CONFIG_PATH = Paths.get(".", "app", "config.yml")
        private val DEFAULT_DATA_PATH = Paths.get(".", "app", "data")
        private const val DEFAULT_PORT = 43594
    }
}

class RsaConfigProvider @Inject constructor(
    private val config: GameConfig
) : Provider<RsaConfig> {

    override fun get(): RsaConfig {
        val keyPath = config.rsaPath
        if (!Files.exists(keyPath)) {
            error("RSA Key file must be generated and stored in: ${keyPath.toAbsolutePath()}")
        }
        PemReader(Files.newBufferedReader(keyPath)).use { reader ->
            return reader.readRsaConfig()
        }
    }

    private fun PemReader.readRsaConfig(): RsaConfig {
        val pem = readPemObject()
        val keySpec = PKCS8EncodedKeySpec(pem.content)

        Security.addProvider(BouncyCastleProvider())
        val factory = KeyFactory.getInstance("RSA", "BC")

        val privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey
        val exponent = privateKey.privateExponent
        val modulus = privateKey.modulus
        return RsaConfig(exponent, modulus)
    }
}
