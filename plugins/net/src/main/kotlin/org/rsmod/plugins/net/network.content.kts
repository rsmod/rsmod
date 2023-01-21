package org.rsmod.plugins.net

import org.rsmod.plugins.net.service.ServiceChannelInitializer

val serviceInitializer: ServiceChannelInitializer by inject()
serviceInitializer.setUp()
