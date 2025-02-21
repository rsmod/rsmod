package org.rsmod.content.other.canoe.configs

import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias canoe_interfaces = CanoeInterfaces

object CanoeInterfaces : InterfaceReferences() {
    val shaping = find("canoe_shaping", 1848119790)
    val destination = find("canoe_destination", 371484599)
}
