@file:Suppress("UnusedReceiverParameter")

package org.rsmod.plugins.content.gameframe

import org.rsmod.plugins.api.component
import org.rsmod.plugins.api.enum
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.minimap
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedEnum

public val enum.gameframe_fixed_component_map: NamedEnum<NamedComponent, NamedComponent>
    get() = NamedEnum(1129)
public val enum.gameframe_resize_list_component_map: NamedEnum<NamedComponent, NamedComponent>
    get() = NamedEnum(1131)
public val enum.gameframe_fullscreen_component_map: NamedEnum<NamedComponent, NamedComponent>
    get() = NamedEnum(1132)

public val component.gameframe_run_button: NamedComponent get() = interf.minimap.child(27)
