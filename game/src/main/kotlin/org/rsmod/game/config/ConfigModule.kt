package org.rsmod.game.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provider
import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import org.rsmod.game.GameEnv
import org.rsmod.game.model.map.Coordinates
import org.rsmod.util.config.ConfigMap
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject

class ConfigModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<GameConfig>()
            .toProvider<GameConfigProvider>()
            .`in`(scope)

        bind<RsaConfig>()
            .toProvider<RsaConfigProvider>()
            .`in`(scope)

        bind<InternalConfig>()
            .toProvider<InternalConfigProvider>()
            .`in`(scope)
    }
}

class GameConfigProvider @Inject constructor(
    private val mapper: ObjectMapper
) : Provider<GameConfig> {

    override fun get(): GameConfig {
        val config = ConfigMap(mapper).load(CONFIG_PATH)
        val name: String = config["server-name"] ?: DEFAULT_SERVER_NAME
        val dataPath: Path = config.dataPath("data-path")
        val pluginPath: Path = config.pluginPath("plugin-path")
        val port: Int = config["port"] ?: DEFAULT_PORT
        val home: List<Int> = config["home"] ?: DEFAULT_HOME
        val envString: String = config["env"] ?: DEFAULT_ENV.toString()

        val revision: Number = config["revision"] ?: error("Game config revision required.")
        val majorRevision: Int
        val minorRevision: Int

        if (revision is Double) {
            val split = revision.toString().split(".")
            if (split.size == 1) {
                majorRevision = revision.toInt()
                minorRevision = DEFAULT_MINOR_REVISION
            } else {
                majorRevision = split[0].toInt()
                minorRevision = split[1].toInt()
            }
        } else {
            majorRevision = revision as Int
            minorRevision = DEFAULT_MINOR_REVISION
        }

        val env = when {
            envString.contains(TESTING_ENV_IDENTIFIER) -> GameEnv.Testing
            envString.contains(DEVELOPMENT_ENV_IDENTIFIER) -> GameEnv.Development
            envString.contains(PRODUCTION_ENV_IDENTIFIER) -> GameEnv.Production
            else -> DEFAULT_ENV
        }

        return GameConfig(
            name = name,
            majorRevision = majorRevision,
            minorRevision = minorRevision,
            port = port,
            dataPath = dataPath,
            pluginPath = pluginPath,
            home = home.coordinates(),
            env = env
        )
    }

    private fun ConfigMap.dataPath(key: String): Path {
        val path: String = this[key] ?: return DEFAULT_DATA_PATH
        return Path.of(path)
    }

    private fun ConfigMap.pluginPath(key: String): Path {
        val path: String = this[key] ?: return DEFAULT_PLUGIN_PATH
        return Path.of(path)
    }

    private fun List<Int>.coordinates() = when (size) {
        2 -> Coordinates(this[0], this[1])
        3 -> Coordinates(this[0], this[1], this[2])
        else -> error("Invalid array size for coordinates: $this")
    }

    companion object {

        private val CONFIG_PATH = Paths.get(".", "config.yml")
        private val DEFAULT_DATA_PATH = Paths.get(".", "data")
        private val DEFAULT_PLUGIN_PATH = Paths.get(".", "plugins")
        private const val DEFAULT_SERVER_NAME = "RS Mod"
        private const val DEFAULT_PORT = 43594
        private const val DEFAULT_MINOR_REVISION = 1
        private val DEFAULT_HOME = listOf(3200, 3200)
        private val DEFAULT_ENV = GameEnv.Production

        private const val TESTING_ENV_IDENTIFIER = "test"
        private const val DEVELOPMENT_ENV_IDENTIFIER = "dev"
        private const val PRODUCTION_ENV_IDENTIFIER = "prod"
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

class InternalConfigProvider @Inject constructor(
    private val mapper: ObjectMapper,
    private val config: GameConfig
) : Provider<InternalConfig> {

    override fun get(): InternalConfig {
        val configFile = config.internalConfig
        if (!Files.exists(configFile)) {
            return DEFAULT_CONFIG
        }
        val default = DEFAULT_CONFIG
        val config = ConfigMap(mapper).load(configFile)
        val gameTickDelay = config["game-tick-delay"] ?: default.gameTickDelay
        val loginsPerCycle = config["logins-per-cycle"] ?: default.loginsPerCycle
        val logoutsPerCycle = config["logouts-per-cycle"] ?: default.logoutsPerCycle
        val actionsPerCycle = config["actions-per-cycle"] ?: default.actionsPerCycle
        return InternalConfig(
            gameTickDelay = gameTickDelay,
            loginsPerCycle = loginsPerCycle,
            logoutsPerCycle = logoutsPerCycle,
            actionsPerCycle = actionsPerCycle
        )
    }

    companion object {

        private val DEFAULT_CONFIG = InternalConfig(
            gameTickDelay = 600,
            loginsPerCycle = 25,
            logoutsPerCycle = 10,
            actionsPerCycle = 25
        )
    }
}
