package gg.rsmod.util

import com.github.michaelbull.logging.InlineLogger
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

object RsaGenerator {

    private fun keys(bitCount: Int): RsaKey {
        Security.addProvider(BouncyCastleProvider())

        val generator = KeyPairGenerator.getInstance("RSA", "BC")
        generator.initialize(bitCount)

        val keyPair = generator.generateKeyPair()
        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey

        return RsaKey(
            publicExponent = publicKey.publicExponent,
            publicModulus = publicKey.modulus,
            privateKey = privateKey.encoded
        )
    }

    fun create(bitCount: Int, radix: Int, path: Path) {
        if (!Files.exists(path.parent)) {
            Files.createDirectory(path.parent)
        }

        println("Generating RSA keys in directory: ${path.toAbsolutePath()}")

        val key = keys(bitCount)
        try {
            PemWriter(Files.newBufferedWriter(path)).use { writer ->
                writer.writeObject(PemObject("RSA PRIVATE KEY", key.privateKey))
            }

            println("")
            println("Place these keys in the client (find BigInteger(\"10001\" in client code):")
            println("--------------------")
            println("public key: " + key.publicExponent.toString(radix))
            println("modulus: " + key.publicModulus.toString(radix))
            println("")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to write private key to directory: ${path.toAbsolutePath()}")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val bitCount = args[0].toInt()
        val radix = args[1].toInt()
        val path = args[2]
        create(bitCount, radix, Paths.get(path))
    }
}

private data class RsaKey(
    val publicExponent: BigInteger,
    val publicModulus: BigInteger,
    val privateKey: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RsaKey

        if (publicExponent != other.publicExponent) return false
        if (publicModulus != other.publicModulus) return false
        if (!privateKey.contentEquals(other.privateKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = publicExponent.hashCode()
        result = 31 * result + publicModulus.hashCode()
        result = 31 * result + privateKey.contentHashCode()
        return result
    }
}
