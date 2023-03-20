package org.rsmod.plugins.api.net.platform.login

import org.rsmod.plugins.api.net.builder.login.LoginPacketDecoderMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class LoginPlatformPacketDecoders @Inject constructor(
    @LoginDesktopDecoder public val desktop: LoginPacketDecoderMap
)
