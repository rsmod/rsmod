package org.rsmod.game.movement

public enum class MoveSpeed(public val steps: Int) {
    Stationary(127),
    Crawl(0),
    Walk(1),
    Run(2);

    public val processRouteDestination: Boolean
        get() = steps in Crawl.steps..Run.steps
}
