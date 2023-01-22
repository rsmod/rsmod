package org.rsmod.plugins.net.rev.desktop

import org.openrs2.buffer.readIntAlt3
import org.openrs2.buffer.readIntAlt3Reverse
import org.rsmod.plugins.net.login.upstream.LoginPacketRequest
import org.rsmod.plugins.net.rev.platform.LoginPlatformPacketDecoders

val platforms: LoginPlatformPacketDecoders by inject()
val decoders = platforms.desktop

decoders.register<LoginPacketRequest.AuthCode> { buf ->
	val code: Int?
	when (buf.readUnsignedByte().toInt()) {
		1, 3 -> {
			code = buf.readUnsignedMedium()
			buf.skipBytes(Byte.SIZE_BYTES)
		}
		0 -> {
			// TODO: remember device
			code = buf.readUnsignedMedium()
			buf.skipBytes(Byte.SIZE_BYTES)
		}
		2 -> {
			code = null
			buf.skipBytes(Int.SIZE_BYTES)
		}
		else -> code = null
	}
	return@register LoginPacketRequest.AuthCode(code)
}

decoders.register<LoginPacketRequest.CacheChecksum> { buf ->
	val crcs = IntArray(21).apply {
		this[13] = buf.readIntAlt3()
		this[2] = buf.readInt()
		this[19] = buf.readIntAlt3Reverse()
		this[8] = buf.readInt()
		this[5] = buf.readIntAlt3()
		buf.skipBytes(Int.SIZE_BYTES)
		this[1] = buf.readIntLE()
		this[15] = buf.readIntAlt3Reverse()
		this[10] = buf.readInt()
		this[0] = buf.readIntAlt3()
		this[18] = buf.readIntLE()
		this[6] = buf.readIntAlt3()
		this[3] = buf.readIntAlt3Reverse()
		this[11] = buf.readIntLE()
		this[7] = buf.readIntAlt3Reverse()
		this[9] = buf.readInt()
		this[14] = buf.readIntLE()
		this[17] = buf.readIntLE()
		this[20] = buf.readIntAlt3Reverse()
		this[4] = buf.readInt()
		this[12] = buf.readIntLE()
	}
	return@register LoginPacketRequest.CacheChecksum(crcs)
}
