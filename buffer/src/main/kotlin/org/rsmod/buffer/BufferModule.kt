package org.rsmod.buffer

import com.google.inject.AbstractModule
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.PooledByteBufAllocator

public object BufferModule : AbstractModule() {

    override fun configure() {
        bind(ByteBufAllocator::class.java).toInstance(PooledByteBufAllocator.DEFAULT)
    }
}
