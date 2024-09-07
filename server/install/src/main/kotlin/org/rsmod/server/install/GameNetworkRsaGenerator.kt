package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.michaelbull.logging.InlineLogger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import org.rsmod.server.shared.DirectoryConstants

fun main(args: Array<String>): Unit = GameNetworkRsaGenerator().main(args)

class GameNetworkRsaGenerator : CliktCommand(name = "generate-rsa") {
    private val preferredDir by option("-outputDir")
    private val fileName by option("-fileName").default("game.key")

    private val logger = InlineLogger()

    private val cacheDir: Path
        get() = preferredDir?.let { Paths.get(it) } ?: DirectoryConstants.DATA_PATH

    override fun run() {
        cacheDir.createDirectories()
        val file = cacheDir.resolve(fileName)
        logger.info { "Generating RSA key to ${file.absolutePathString()}" }
        create(file)
        logger.info { "Generated RSA key." }
    }

    private fun create(output: Path, bitLength: Int = 1024) {
        Security.addProvider(BouncyCastleProvider())

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
        keyPairGenerator.initialize(bitLength)
        val keyPair = keyPairGenerator.generateKeyPair()

        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey

        println("")
        println("Place these keys in the client (find BigInteger(\"10001\" in client code):")
        println("--------------------")
        println("public key: " + publicKey.publicExponent.toString(16))
        println("modulus: " + publicKey.modulus.toString(16))
        println("")

        try {
            PemWriter(Files.newBufferedWriter(output)).use { writer ->
                writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to write private key to ${output.toAbsolutePath()}" }
        }
    }
}
