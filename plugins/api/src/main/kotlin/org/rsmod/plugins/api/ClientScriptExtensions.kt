package org.rsmod.plugins.api

import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.net.downstream.RunClientScript
import org.rsmod.plugins.types.NamedScript
import org.rsmod.plugins.types.ScriptTypeList1
import org.rsmod.plugins.types.ScriptTypeList10
import org.rsmod.plugins.types.ScriptTypeList2
import org.rsmod.plugins.types.ScriptTypeList3
import org.rsmod.plugins.types.ScriptTypeList4
import org.rsmod.plugins.types.ScriptTypeList5
import org.rsmod.plugins.types.ScriptTypeList6
import org.rsmod.plugins.types.ScriptTypeList7
import org.rsmod.plugins.types.ScriptTypeList8
import org.rsmod.plugins.types.ScriptTypeList9

public fun <T1 : Any> Player.clientScript(script: NamedScript<ScriptTypeList1<T1>>, arg1: T1): Unit =
    runClientScript(script.id, arg1)

public fun <T1 : Any, T2 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList2<T1, T2>>,
    arg1: T1,
    arg2: T2
): Unit = runClientScript(script.id, arg1, arg2)

public fun <T1 : Any, T2 : Any, T3 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList3<T1, T2, T3>>,
    arg1: T1,
    arg2: T2,
    arg3: T3
): Unit = runClientScript(script.id, arg1, arg2, arg3)

public fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList4<T1, T2, T3, T4>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4)

public fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList5<T1, T2, T3, T4, T5>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4,
    arg5: T5
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4, arg5)

public fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList6<T1, T2, T3, T4, T5, T6>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4,
    arg5: T5,
    arg6: T6
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4, arg5, arg6)

public fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList7<T1, T2, T3, T4, T5, T6, T7>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4,
    arg5: T5,
    arg6: T6,
    arg7: T7
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4, arg5, arg6, arg7)

public fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any> Player.clientScript(
    script: NamedScript<ScriptTypeList8<T1, T2, T3, T4, T5, T6, T7, T8>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4,
    arg5: T5,
    arg6: T6,
    arg7: T7,
    arg8: T8
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)

public fun <
    T1 : Any,
    T2 : Any,
    T3 : Any,
    T4 : Any,
    T5 : Any,
    T6 : Any,
    T7 : Any,
    T8 : Any,
    T9 : Any
    > Player.clientScript(
    script: NamedScript<ScriptTypeList9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4,
    arg5: T5,
    arg6: T6,
    arg7: T7,
    arg8: T8,
    arg9: T9
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)

public fun <
    T1 : Any,
    T2 : Any,
    T3 : Any,
    T4 : Any,
    T5 : Any,
    T6 : Any,
    T7 : Any,
    T8 : Any,
    T9 : Any,
    T10 : Any
    > Player.clientScript(
    script: NamedScript<ScriptTypeList10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>,
    arg1: T1,
    arg2: T2,
    arg3: T3,
    arg4: T4,
    arg5: T5,
    arg6: T6,
    arg7: T7,
    arg8: T8,
    arg9: T9,
    arg10: T10
): Unit = runClientScript(script.id, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)

/**
 * This function should be _avoided_ when possible. You should only resort to using
 * it if none of the [clientScript] function signatures match the client script you
 * are trying to send.
 */
public fun Player.runClientScript(id: Int, vararg args: Any) {
    downstream += RunClientScript(id, args.toList())
}
