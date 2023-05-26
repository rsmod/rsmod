package org.rsmod.game.ui

public data class UserInterfaceMap(
    public val topLevel: MutableSet<UserInterface> = mutableSetOf(),
    public val overlays: MutableMap<Component, UserInterface> = mutableMapOf(),
    public val modals: MutableMap<Component, UserInterface> = mutableMapOf(),
    public val properties: MutableMap<Component, ComponentProperty> = mutableMapOf(),
    public val gameframe: MutableMap<Component, Component> = mutableMapOf()
)
