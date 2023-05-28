package org.rsmod.game.model.mob

import org.rsmod.game.model.BuildArea
import org.rsmod.game.model.DownstreamList
import org.rsmod.game.model.EventList
import org.rsmod.game.model.UpstreamList
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.ui.UserInterfaceMap

public class Player(override val entity: PlayerEntity = PlayerEntity()) : Mob() {

    public val upstream: UpstreamList = UpstreamList()

    public val downstream: DownstreamList = DownstreamList()

    public val events: EventList<Player> = EventList()

    public val ui: UserInterfaceMap = UserInterfaceMap()

    public var displayName: String
        get() = entity.name
        set(value) { entity.name = value }

    public var username: String = ""

    public var buildArea: BuildArea = BuildArea.ZERO

    public override fun toString(): String {
        return "Player(username=$username, displayName=$displayName, coords=$coords)"
    }
}
