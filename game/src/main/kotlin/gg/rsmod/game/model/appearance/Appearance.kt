package gg.rsmod.game.model.appearance

data class Appearance(
    val gender: Int,
    val skullIcon: Int,
    val overheadPrayer: Int,
    val npcTransform: Int,
    val body: List<Int>,
    val colors: List<Int>,
    val bas: List<Int>
) {

    companion object {

        val ZERO = Appearance(
            gender = 0,
            skullIcon = 0,
            overheadPrayer = 0,
            npcTransform = 0,
            body = emptyList(),
            colors = emptyList(),
            bas = emptyList()
        )
    }
}
