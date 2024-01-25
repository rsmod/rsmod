package org.rsmod.plugins.net.js5

import com.google.common.util.concurrent.AbstractExecutionThreadService
import io.netty.buffer.ByteBufAllocator
import org.openrs2.buffer.use
import org.openrs2.cache.Js5Compression
import org.openrs2.cache.Js5CompressionType
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store
import org.openrs2.cache.VersionTrailer
import org.rsmod.plugins.api.cache.build.js5.Js5Cache
import org.rsmod.plugins.net.js5.downstream.Js5GroupResponse
import org.rsmod.plugins.net.js5.upstream.Js5Request
import java.io.FileNotFoundException
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
public class Js5Service @Inject constructor(
    @Js5Cache private val store: Store,
    private val masterIndex: Js5MasterIndex,
    private val alloc: ByteBufAllocator
) : AbstractExecutionThreadService() {

    private val lock = Object()
    private val clients = ArrayDeque<Js5Client>()

    override fun run() {
        while (true) {
            var client: Js5Client
            var request: Js5Request.Group
            synchronized(lock) {
                while (true) {
                    if (!isRunning) return
                    val head = clients.removeFirstOrNull()
                    if (head == null) {
                        lock.wait()
                        continue
                    }
                    client = head
                    request = client.pop() ?: continue
                    break
                }
            }
            serve(client, request)
        }
    }

    @Suppress("UnstableApiUsage")
    override fun triggerShutdown() {
        synchronized(lock) {
            lock.notifyAll()
        }
    }

    public fun push(client: Js5Client, request: Js5Request.Group) {
        synchronized(lock) {
            client.push(request)
            if (client.isReady()) {
                if (!clients.contains(client)) clients += client
                lock.notifyAll()
            }
            if (client.isNotFull()) {
                client.ctx.read()
            }
        }
    }

    public fun readIfNotFull(client: Js5Client) {
        synchronized(lock) {
            if (client.isNotFull()) {
                client.ctx.read()
            }
        }
    }

    public fun notifyIfNotEmpty(client: Js5Client) {
        synchronized(lock) {
            if (client.isNotEmpty()) {
                lock.notifyAll()
            }
        }
    }

    private fun serve(client: Js5Client, request: Js5Request.Group) {
        val ctx = client.ctx
        if (!ctx.channel().isActive) return
        val buf = if (request.archive == Store.ARCHIVESET && request.group == Store.ARCHIVESET) {
            alloc.buffer().use { uncompressed ->
                masterIndex.write(uncompressed)
                Js5Compression.compress(uncompressed, Js5CompressionType.UNCOMPRESSED).use { compressed ->
                    compressed.retain()
                }
            }
        } else {
            try {
                store.read(request.archive, request.group).use { buf ->
                    if (request.archive != Store.ARCHIVESET) {
                        VersionTrailer.strip(buf)
                    }
                    buf.retain()
                }
            } catch (ex: FileNotFoundException) {
                ctx.close()
                return
            }
        }
        val response = Js5GroupResponse(
            archive = request.archive,
            group = request.group,
            urgent = request.urgent,
            data = buf
        )
        ctx.writeAndFlush(response, ctx.voidPromise())
        synchronized(lock) {
            if (client.isReady()) {
                clients += client
            }
            if (client.isNotFull()) {
                ctx.read()
            }
        }
    }
}
