@file:Suppress("UNUSED")

package org.rsmod.plugins.types

@JvmInline
public value class NamedScript<T : ScriptTypeList>(public val id: Int)

public interface ScriptTypeList
public class ScriptTypeList1<T1> : ScriptTypeList
public class ScriptTypeList2<T1, T2> : ScriptTypeList
public class ScriptTypeList3<T1, T2, T3> : ScriptTypeList
public class ScriptTypeList4<T1, T2, T3, T4> : ScriptTypeList
public class ScriptTypeList5<T1, T2, T3, T4, T5> : ScriptTypeList
public class ScriptTypeList6<T1, T2, T3, T4, T5, T6> : ScriptTypeList
public class ScriptTypeList7<T1, T2, T3, T4, T5, T6, T7> : ScriptTypeList
public class ScriptTypeList8<T1, T2, T3, T4, T5, T6, T7, T8> : ScriptTypeList
public class ScriptTypeList9<T1, T2, T3, T4, T5, T6, T7, T8, T9> : ScriptTypeList
public class ScriptTypeList10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> : ScriptTypeList

public interface ScriptTypeArguments {

    public val args: Array<out Any>
}
