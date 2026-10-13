package org.runerealm.server.app

import io.netty.buffer.Unpooled
import org.openrs2.cache.Cache

/**
 * Rewrites hard-coded client text that names the game world, where the new text has a different
 * length than the original (so it cannot be swapped byte-for-byte in place).
 *
 * The bank title is not sent by the server: `[proc,bankmain_build]` (script 277) sets it to "The
 * Bank of Gielinor", and the deposit box frame (interface 192) passes "The Bank of Gielinor -
 * Deposit Box" to its `steelborder` onload script. Both are NUL-terminated string operands, and
 * neither client scripts nor interface definitions store byte offsets to them (script jumps are
 * instruction indices and the script trailer is located from the end of the file), so a string can
 * safely grow.
 */
internal object ClientTextOverrides {
    private const val INTERFACE_ARCHIVE = 3
    private const val CLIENTSCRIPT_ARCHIVE = 12

    /** Longest first, so a longer string is not partially matched by a shorter one. */
    private val REPLACEMENTS: List<Pair<ByteArray, ByteArray>> =
        listOf(
                "The Bank of Gielinor - Deposit Box" to "The Bank of RuneRealm - Deposit Box",
                "The Bank of Gielinor - Item Deposits" to "The Bank of RuneRealm - Item Deposits",
                "The Bank of Gielinor" to "The Bank of RuneRealm",
            )
            .map { (old, new) ->
                old.toByteArray(Charsets.ISO_8859_1) to new.toByteArray(Charsets.ISO_8859_1)
            }
            .sortedByDescending { it.first.size }

    /** Returns the number of cache files that were rewritten. */
    fun apply(cache: Cache): Int {
        // Read everything first and write afterwards: openrs2 cannot read a group back once it has
        // been written in the same session, and interface groups hold many files.
        val pending = mutableListOf<PendingWrite>()
        for (archive in intArrayOf(INTERFACE_ARCHIVE, CLIENTSCRIPT_ARCHIVE)) {
            if (!cache.exists(archive)) {
                continue
            }
            for (group in cache.list(archive).asSequence().toList()) {
                for (file in cache.list(archive, group.id).asSequence().toList()) {
                    val bytes =
                        cache.read(archive, group.id, file.id).let { buf ->
                            try {
                                ByteArray(buf.readableBytes()).also(buf::readBytes)
                            } finally {
                                buf.release()
                            }
                        }
                    val replaced = replaceStrings(bytes) ?: continue
                    pending += PendingWrite(archive, group.id, file.id, replaced)
                }
            }
        }
        for ((archive, group, file, bytes) in pending) {
            cache.write(archive, group, file, Unpooled.wrappedBuffer(bytes))
        }
        return pending.size
    }

    private data class PendingWrite(
        val archive: Int,
        val group: Int,
        val file: Int,
        val bytes: ByteArray,
    )

    /**
     * Replaces every whole NUL-terminated string in [bytes] that equals one of [REPLACEMENTS]'s
     * originals. Returns `null` when nothing matched.
     */
    internal fun replaceStrings(bytes: ByteArray): ByteArray? {
        var out: java.io.ByteArrayOutputStream? = null
        var copiedUpTo = 0
        var i = 0
        while (i < bytes.size) {
            val match = REPLACEMENTS.firstOrNull { (old, _) -> bytes.isWholeStringAt(old, i) }
            if (match == null) {
                i++
                continue
            }
            val stream = out ?: java.io.ByteArrayOutputStream(bytes.size + 64).also { out = it }
            stream.write(bytes, copiedUpTo, i - copiedUpTo)
            stream.write(match.second)
            i += match.first.size
            copiedUpTo = i
        }
        val stream = out ?: return null
        stream.write(bytes, copiedUpTo, bytes.size - copiedUpTo)
        return stream.toByteArray()
    }

    private fun ByteArray.isWholeStringAt(text: ByteArray, offset: Int): Boolean {
        val end = offset + text.size
        if (end >= size || this[end] != 0.toByte()) {
            return false
        }
        // The string must start right after a non-text byte (an opcode or argument type byte),
        // not in the middle of a longer string.
        if (offset > 0 && this[offset - 1].toInt().toChar() in ' '..'~') {
            return false
        }
        for (j in text.indices) {
            if (this[offset + j] != text[j]) {
                return false
            }
        }
        return true
    }
}
