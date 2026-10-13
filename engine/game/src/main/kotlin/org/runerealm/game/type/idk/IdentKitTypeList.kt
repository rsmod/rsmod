package org.runerealm.game.type.idk

public class IdentKitTypeList(public val types: Map<Int, IdentKitType>) :
    Map<Int, IdentKitType> by types {
    private val selectableByBodyPart: Map<Int, List<Int>> =
        types.values.filter(IdentKitType::selectable).groupBy(IdentKitType::bodyPart).mapValues {
            (_, kits) ->
            kits.map(IdentKitType::id).sorted()
        }

    /** Returns the ids of selectable kits for [bodyPart], ordered by id. */
    public fun selectable(bodyPart: Int): List<Int> = selectableByBodyPart[bodyPart].orEmpty()
}
