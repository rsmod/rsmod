package org.rsmod.plugins.api.net.platform.info

import org.rsmod.plugins.api.net.builder.info.ExtendedInfoEncoderMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class InfoPlatformPacketEncoders @Inject constructor(
    @InfoDesktopEncoder public val desktop: ExtendedInfoEncoderMap
)
