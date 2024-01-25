package org.rsmod.plugins.api.net.platform.info

import org.rsmod.plugins.api.net.builder.info.ExtendedInfoEncoderMap
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
public class InfoPlatformPacketEncoders @Inject constructor(
    @InfoDesktopEncoder public val desktop: ExtendedInfoEncoderMap
)
