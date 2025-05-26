//enum class SmeltingData(
//    val product: Int,
//    val primaryOre: Int,
//    val secondaryOre: Int = Items.COAL,
//    val secondaryCount: Int = 1,
//    val levelRequired: Int,
//    val experience: Double,
//) {
//    BRONZE(
//        product = Items.BRONZE_BAR,
//        primaryOre = Items.TIN_ORE,
//        secondaryOre = Items.COPPER_ORE,
//        levelRequired = 1,
//        experience = 6.2,
//    ),
//    BLURITE(
//        product = Items.BLURITE_BAR,
//        primaryOre = Items.BLURITE_ORE,
//        secondaryCount = 0,
//        levelRequired = 8,
//        experience = 8.0,
//    ),
//    IRON(
//        product = Items.IRON_BAR,
//        primaryOre = Items.IRON_ORE,
//        secondaryCount = 0,
//        levelRequired = 15,
//        experience = 12.5,
//    ),
//    SILVER(
//        product = Items.SILVER_BAR,
//        primaryOre = Items.SILVER_ORE,
//        secondaryCount = 0,
//        levelRequired = 20,
//        experience = 13.7,
//    ),
//    STEEL(
//        product = Items.STEEL_BAR,
//        primaryOre = Items.IRON_ORE,
//        secondaryCount = 2,
//        levelRequired = 30,
//        experience = 17.5,
//    ),
//    GOLD(
//        product = Items.GOLD_BAR,
//        primaryOre = Items.GOLD_ORE,
//        secondaryCount = 0,
//        levelRequired = 40,
//        experience = 22.5,
//    ),
//    MITHRIL(
//        product = Items.MITHRIL_BAR,
//        primaryOre = Items.MITHRIL_ORE,
//        secondaryCount = 4,
//        levelRequired = 50,
//        experience = 30.0,
//    ),
//    ADAMANT(
//        product = Items.ADAMANT_BAR,
//        primaryOre = Items.ADAMANTITE_ORE,
//        secondaryCount = 6,
//        levelRequired = 70,
//        experience = 37.5,
//    ),
//    RUNE(
//        product = Items.RUNE_BAR,
//        primaryOre = Items.RUNITE_ORE,
//        secondaryCount = 8,
//        levelRequired = 85,
//        experience = 50.0,
//    ),
//    ;
//
//    companion object {
//        /**
//         * The cached array of enum definitions
//         */
//        val values = enumValues<SmeltingData>()
//
//        /**
//         * The map of bar ids to their definitions
//         */
//        val barDefinitions = values.associate { it.product to it }
//    }
//}
