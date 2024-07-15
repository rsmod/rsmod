package org.rsmod.plugins.net.login.downstream

import jakarta.inject.Singleton
import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec

@Singleton
public class InvalidCredentialsCodec : ZeroLengthPacketCodec<LoginResponse.InvalidCredentials>(
    packet = LoginResponse.InvalidCredentials,
    opcode = 3
)
