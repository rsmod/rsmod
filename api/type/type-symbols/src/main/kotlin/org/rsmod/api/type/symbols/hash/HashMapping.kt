package org.rsmod.api.type.symbols.hash

public data class HashMapping(
    public val clientscripts: Map<Long, String> = mutableMapOf(),
    public val components: Map<Long, String> = mutableMapOf(),
    public val enums: Map<Long, String> = mutableMapOf(),
    public val interfaces: Map<Long, String> = mutableMapOf(),
    public val invs: Map<Long, String> = mutableMapOf(),
    public val locs: Map<Long, String> = mutableMapOf(),
    public val models: Map<Long, String> = mutableMapOf(),
    public val npcs: Map<Long, String> = mutableMapOf(),
    public val objs: Map<Long, String> = mutableMapOf(),
    public val params: Map<Long, String> = mutableMapOf(),
    public val seqs: Map<Long, String> = mutableMapOf(),
    public val stats: Map<Long, String> = mutableMapOf(),
    public val structs: Map<Long, String> = mutableMapOf(),
    public val synths: Map<Long, String> = mutableMapOf(),
    public val varbits: Map<Long, String> = mutableMapOf(),
    public val varps: Map<Long, String> = mutableMapOf(),
    public val varns: Map<Long, String> = mutableMapOf(),
    public val varnbits: Map<Long, String> = mutableMapOf(),
)
