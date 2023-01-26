package org.rsmod.plugins.net.rev.platform

import org.rsmod.plugins.api.prot.builder.login.LoginPacketDecoderMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginPlatformPacketDecoders @Inject constructor(
    @LoginDesktopDecoder val desktop: LoginPacketDecoderMap
)
