package org.rsmod.util.security

import org.mindrot.jbcrypt.BCrypt

class BCryptEncryption : PasswordEncryption {

    override fun encrypt(plainText: String): String {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(LOG_ROUNDS))
    }

    override fun verify(plainText: String, encrypted: String): Boolean {
        return BCrypt.checkpw(plainText, encrypted)
    }

    companion object {
        private const val LOG_ROUNDS = 16
    }
}
