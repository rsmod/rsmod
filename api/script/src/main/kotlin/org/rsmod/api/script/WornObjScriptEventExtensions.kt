package org.rsmod.api.script

import org.rsmod.api.player.events.interact.WornObjContentEvents
import org.rsmod.api.player.events.interact.WornObjEvents
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.ScriptContext

public fun ScriptContext.onWornObj2(type: ObjType, action: WornObjEvents.Op2.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj3(type: ObjType, action: WornObjEvents.Op3.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj4(type: ObjType, action: WornObjEvents.Op4.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj5(type: ObjType, action: WornObjEvents.Op5.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj6(type: ObjType, action: WornObjEvents.Op6.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj7(type: ObjType, action: WornObjEvents.Op7.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj8(type: ObjType, action: WornObjEvents.Op8.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj9(type: ObjType, action: WornObjEvents.Op9.() -> Unit): Unit =
    onEvent(type.id, action)

public fun ScriptContext.onWornObj2(
    content: ContentGroupType,
    action: WornObjContentEvents.Op2.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj3(
    content: ContentGroupType,
    action: WornObjContentEvents.Op3.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj4(
    content: ContentGroupType,
    action: WornObjContentEvents.Op3.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj5(
    content: ContentGroupType,
    action: WornObjContentEvents.Op5.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj6(
    content: ContentGroupType,
    action: WornObjContentEvents.Op6.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj7(
    content: ContentGroupType,
    action: WornObjContentEvents.Op7.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj8(
    content: ContentGroupType,
    action: WornObjContentEvents.Op8.() -> Unit,
): Unit = onEvent(content.id, action)

public fun ScriptContext.onWornObj9(
    content: ContentGroupType,
    action: WornObjContentEvents.Op9.() -> Unit,
): Unit = onEvent(content.id, action)
