package org.rsmod.plugins.content.area.tutorial

import org.rsmod.game.model.map.MapIsolation
import org.rsmod.game.model.map.MapSquare

val mapIsolation: MapIsolation by inject()

mapIsolation.register {
    mapSquare = MapSquare(id = 12336)
    hidden {
        -MapSquare(id = 12081)
        -MapSquare(id = 12337)
        -MapSquare(id = 12591)
        -MapSquare(id = 12593)
    }
}

mapIsolation.register {
    mapSquare = MapSquare(id = 12592)
    hidden {
        -MapSquare(id = 12591)
    }
}

mapIsolation.register {
    mapSquare = MapSquare(id = 12436)
    hidden {
        -MapSquare(id = 12179)
        -MapSquare(id = 12181)
        -MapSquare(id = 12435)
    }
}
