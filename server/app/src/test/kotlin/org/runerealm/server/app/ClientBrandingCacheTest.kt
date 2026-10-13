package org.runerealm.server.app

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.openrs2.cache.Cache

class ClientBrandingCacheTest {
    @Test
    fun `renaming RuneScape leaves no readable occurrences and can rerun`(@TempDir dir: Path) {
        val source = findJs5Cache()
        assumeTrue(source != null, "No local js5 cache to test against")
        val js5 = dir.resolve("js5").createDirectories()
        for (file in checkNotNull(source).listDirectoryEntries()) {
            file.copyTo(js5.resolve(file.fileName))
        }

        ClientBranding.apply(js5)
        // A second run must succeed (nothing left to rename) and the cache must stay readable.
        ClientBranding.apply(js5)

        Cache.open(js5).use { cache ->
            var found = false
            var renamed = false
            for (archive in intArrayOf(2, 3, 12)) {
                for (group in cache.list(archive).asSequence().toList()) {
                    for (file in cache.list(archive, group.id).asSequence().toList()) {
                        val buf = cache.read(archive, group.id, file.id)
                        val bytes = ByteArray(buf.readableBytes()).also(buf::readBytes)
                        buf.release()
                        val text = String(bytes, Charsets.ISO_8859_1)
                        if (Regex("(?<![\\w./])RuneScape(?![\\w.])").containsMatchIn(text)) {
                            found = true
                        }
                        if ("Welcome to Old School RuneRealm" in text) {
                            renamed = true
                        }
                    }
                }
            }
            assertFalse(found)
            assertTrue(renamed)
        }
    }

    private fun findJs5Cache(): Path? {
        var dir: Path? = Paths.get("").toAbsolutePath()
        while (dir != null) {
            val js5 = dir.resolve(".data/cache/js5")
            if (js5.isDirectory() && Files.list(js5).use { it.findAny().isPresent }) {
                return js5
            }
            dir = dir.parent
        }
        return null
    }
}
