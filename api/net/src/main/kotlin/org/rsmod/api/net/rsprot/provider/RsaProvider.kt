package org.rsmod.api.net.rsprot.provider

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import net.rsprot.crypto.rsa.RsaKeyPair
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader

object RsaProvider {
    fun from(file: Path): RsaKeyPair = Files.newBufferedReader(file).toKeyPair()

    private fun BufferedReader.toKeyPair(): RsaKeyPair =
        PemReader(this).use { reader ->
            val pem = reader.readPemObject()
            val keySpec = PKCS8EncodedKeySpec(pem.content)
            Security.addProvider(BouncyCastleProvider())
            val factory = KeyFactory.getInstance("RSA", "BC")
            val privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey
            val exponent = privateKey.privateExponent
            val modulus = privateKey.modulus
            RsaKeyPair(exponent, modulus)
        }
}
