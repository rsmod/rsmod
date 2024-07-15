package org.rsmod.plugins.api.net.platform.info

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.plugins.api.net.builder.info.ExtendedInfoEncoderMap

@Singleton
public class InfoPlatformPacketEncoders @Inject constructor(
    @InfoDesktopEncoder public val desktop: ExtendedInfoEncoderMap
)
