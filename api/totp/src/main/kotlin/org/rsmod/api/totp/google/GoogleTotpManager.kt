package org.rsmod.api.totp.google

import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import java.time.Instant
import java.util.Date
import org.apache.commons.codec.binary.Base32
import org.rsmod.api.totp.TotpManager

public class GoogleTotpManager : TotpManager {
    override fun verifyCode(secret: CharArray, code: String, now: Instant): Boolean {
        val decodedSecret = Base32().decode(String(secret))
        val authenticator = GoogleAuthenticator(decodedSecret)
        return authenticator.isValid(code, Date.from(now))
    }

    override fun generateSecret(): CharArray {
        val plaintextSecret = GoogleAuthenticator.createRandomSecretAsByteArray()
        val base32 = Base32().encodeToString(plaintextSecret)
        return base32.toCharArray()
    }
}
