package org.rsmod.api.utils.io

import java.io.IOException
import java.io.InputStream

public object InputStreams {
    public inline fun <reified T> resourceInputOrNull(fileName: String): InputStream? =
        T::class.java.getResourceAsStream(fileName)

    public inline fun <reified T> resourceInput(fileName: String): InputStream =
        resourceInputOrNull<T>(fileName)
            ?: throw IOException("Failed to load input stream for `$fileName`.")

    public inline fun <reified T> readAllBytes(resourceFileName: String): ByteArray =
        resourceInput<T>(resourceFileName).use { it.readBytes() }
}
