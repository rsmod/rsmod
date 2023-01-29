package org.rsmod.plugins.api.net.platform

import org.rsmod.plugins.api.net.builder.login.LoginPacketDecoderMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginPlatformPacketDecoders @Inject constructor(
    @LoginDesktopDecoder val desktop: LoginPacketDecoderMap
)
