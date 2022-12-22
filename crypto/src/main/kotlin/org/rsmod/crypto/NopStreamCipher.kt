package org.rsmod.crypto

public object NopStreamCipher : StreamCipher {

    override fun nextInt(): Int {
        return 0
    }
}
