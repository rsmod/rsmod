package org.rsmod.plugins.api.net.platform.login

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.plugins.api.net.builder.login.LoginPacketDecoderMap

@Singleton
public class LoginPlatformPacketDecoders @Inject constructor(
    @LoginDesktopDecoder public val desktop: LoginPacketDecoderMap
)
