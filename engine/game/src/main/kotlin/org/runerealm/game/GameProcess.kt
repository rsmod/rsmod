package org.runerealm.game

public interface GameProcess {
    public fun startup()

    public fun cycle()

    public fun preShutdown()

    public fun shutdown()
}
