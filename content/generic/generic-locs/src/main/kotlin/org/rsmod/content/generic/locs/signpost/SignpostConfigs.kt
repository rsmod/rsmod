package org.rsmod.content.generic.locs.signpost

import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.map.CoordGrid

internal object SignpostLocs : LocReferences() {
    val signpost = find("signpost", 5944252288185964868)
}

internal object SignpostInterfaces : InterfaceReferences() {
    val signpost = find("signpost", 9223372036367484796)
}

internal object SignpostComponents : ComponentReferences() {
    val signpost_north = find("signpost_com2", 3633456669255458235)
    val signpost_east = find("signpost_com7", 6865859231034431206)
    val signpost_south = find("signpost_com8", 3946237333876253083)
    val signpost_west = find("signpost_com11", 2578427790880957538)
}

internal object SignpostEnums : EnumReferences() {
    val signpost_directions = find<CoordGrid, String>("signpost_directions")
}

internal object SignpostEnumBuild : EnumBuilder() {
    init {
        build<CoordGrid, String>("signpost_directions") {
            default =
                "West to the Kingdom of Zeah." +
                    "|South to the City of Lumbridge." +
                    "|North to the Wilderness." +
                    "|East to Fossil Island."

            val lumbridgeCastle = CoordGrid(0, 50, 50, 35, 28)
            this[lumbridgeCastle] =
                "West to the Lumbridge Castle and Draynor Village. Beware the goblins!" +
                    "|South to the swamps of Lumbridge." +
                    "|Head north towards Fred's farm and the windmill." +
                    "|Cross the bridge and head east to Al Kharid or north to Varrock."

            val lumbridgeGoblinArea = CoordGrid(0, 50, 50, 61, 30)
            this[lumbridgeGoblinArea] =
                "West to Lumbridge." +
                    "|The River Lum lies to the south." +
                    "|North to farms and Varrock." +
                    "|East to Al Kharid - toll gate; bring some money."

            val draynor = CoordGrid(0, 48, 51, 35, 32)
            this[draynor] =
                "West to Port Sarim, Falador and Rimmington." +
                    "|South to Draynor Village and the Wizards' Tower." +
                    "|North to Draynor Manor." +
                    "|East to Lumbridge."

            val rimmington = CoordGrid(0, 46, 51, 39, 14)
            this[rimmington] =
                "Follow the path west to the Crafting Guild." +
                    "|Follow the path south to Rimmington." +
                    "|North to the glorious White Knights' city of Falador." +
                    "|Follow the path east to Port Sarim and Draynor Village."
        }
    }
}
