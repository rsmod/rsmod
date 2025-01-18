package org.rsmod.server.install

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
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
import kotlin.io.path.exists
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import org.rsmod.server.shared.DirectoryConstants

fun main(args: Array<String>): Unit = GameNetworkRsaGenerator().main(args)

class GameNetworkRsaGenerator : CliktCommand(name = "generate-rsa") {
    private val preferredDir by option("-outputDir")
    private val privateKeyFileName by option("-privateKeyFile").default("game.key")
    private val publicModFileName by option("-publicModFile").default("client.key")
    private val fileOverwrite by option("-fileOverwrite").flag(default = false)

    private val logger = InlineLogger()

    private val cacheDir: Path
        get() = preferredDir?.let { Paths.get(it) } ?: DirectoryConstants.DATA_PATH

    override fun run() {
        if (!fileOverwrite) {
            val foundFile = cacheDir.resolve(privateKeyFileName)
            if (foundFile.exists()) {
                logger.info { "RSA key file already found: $foundFile" }
                return
            }
        }
        cacheDir.createDirectories()
        val gameKeyFile = cacheDir.resolve(privateKeyFileName)
        val clientModFile = cacheDir.resolve(publicModFileName)
        logger.info { "Generating RSA key to ${gameKeyFile.absolutePathString()}" }
        create(gameKeyFile, clientModFile)
        logger.info { "Generated RSA key." }
    }

    private fun create(privateKeyFile: Path, pubModFile: Path, bitLength: Int = 1024) {
        Security.addProvider(BouncyCastleProvider())

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
        keyPairGenerator.initialize(bitLength)
        val keyPair = keyPairGenerator.generateKeyPair()

        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey

        val publicExponent = publicKey.publicExponent.toString(16)
        val publicModulus = publicKey.modulus.toString(16)

        PemWriter(Files.newBufferedWriter(privateKeyFile)).use { writer ->
            writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
        }

        Files.newBufferedWriter(pubModFile).use { writer ->
            writer.write("Exponent: $publicExponent")
            writer.newLine()
            writer.write("Modulus: $publicModulus")
        }

        println()
        println("Place these keys in the client (find BigInteger(\"10001\" in client code):")
        println("--------------------")
        println("exponent: $publicExponent")
        println("modulus: $publicModulus")
        println("--------------------")
        println("Additionally, these details have been written to ${pubModFile.toAbsolutePath()}")
        println()
    }
}
