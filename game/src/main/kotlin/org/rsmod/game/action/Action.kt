package org.rsmod.game.action

interface Action

typealias ActionExecutor<T> = (T).() -> Unit
