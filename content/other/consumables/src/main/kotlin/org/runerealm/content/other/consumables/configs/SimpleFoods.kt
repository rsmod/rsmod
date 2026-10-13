@file:Suppress("SpellCheckingInspection")

package org.runerealm.content.other.consumables.configs

/**
 * Single-bite foods that only heal, generated from the OSRS Wiki's
 * [Food/All food](https://oldschool.runescape.wiki/w/Food/All_food) table and matched to cache objs
 * (with an "Eat" option) by name. Foods with additional effects are not included here.
 *
 * Maps obj internal name to Hitpoints healed.
 */
internal val SIMPLE_FOOD_HEALS: Map<String, Int> =
    mapOf(
        "anchovies" to 1, // Anchovies
        "baguette" to 6, // Baguette
        "potato_baked" to 4, // Baked potato
        "banana" to 2, // Banana
        "mm_banana_stew" to 11, // Banana stew
        "bass" to 13, // Bass
        "dorgesh_bat_shish" to 2, // Bat shish
        "blueberry_muffin" to 4, // Blueberry muffin
        "raids_fish3_cooked" to 14, // Brawk fish (3)
        "bread" to 5, // Bread
        "magic_cabbage" to 1, // Cabbage
        "cabbage" to 1, // Cabbage
        "brut_caviar" to 5, // Caviar
        "cheese" to 2, // Cheese
        "aluft_cheese+tom_batta" to 11, // Cheese+tom batta
        "cheese+tom_batta" to 11, // Cheese+tom batta
        "bowl_chilli+carne" to 5, // Chilli con carne
        "potato_chilli+carne" to 14, // Chilli potato
        "aluft_chocchip_crunchies" to 7, // Chocchip crunchies
        "chocchip_crunchies" to 7, // Chocchip crunchies
        "chocolate_bar" to 3, // Chocolate bar
        "chocolate_bomb" to 15, // Chocolate bomb
        "aluft_chocolate_bomb" to 15, // Chocolate bomb
        "bowl_onion" to 1, // Chopped onion
        "bowl_tomato" to 2, // Chopped tomato
        "bowl_tuna" to 10, // Chopped tuna
        "dorgesh_crispy_froglegs" to 2, // Coated frogs' legs
        "cod" to 7, // Cod
        "cooked_chicken_undead" to 3, // Cooked chicken
        "cooked_chicken" to 3, // Cooked chicken
        "chompy_cooked" to 11, // Cooked chompy
        "cooked_chompy" to 11, // Cooked chompy
        "hundred_pirate_fishcake" to 11, // Cooked fishcake
        "100_jubbly_meat_cooked" to 15, // Cooked jubbly
        "cooked_meat" to 3, // Cooked meat
        "cooked_meat_undead" to 3, // Cooked meat
        "cooked_mystery_meat" to 5, // Cooked mystery meat
        "cooked_oomlie" to 14, // Cooked oomlie wrap
        "cooked_rabbit" to 5, // Cooked rabbit
        "gauntlet_combo_food_hm" to 16, // Corrupted paddlefish
        "gauntlet_combo_food" to 16, // Crystal paddlefish
        "curry" to 19, // Curry
        "dark_crab" to 22, // Dark crab
        "dragonfruit" to 10, // Dragonfruit
        "easter_egg" to 14, // Easter egg
        "edible_seaweed" to 4, // Edible seaweed
        "dorgesh_cave_eel_sushi" to 10, // Eel sushi
        "bowl_egg+tomato" to 8, // Egg and tomato
        "potato_egg+tomato" to 16, // Egg potato
        "equa_leaves" to 1, // Equa leaves
        "pest_field_ration" to 10, // Field ration
        "dorgesh_cave_crawler_fillets" to 2, // Fillets
        "dorgesh_wall_beast_fingers" to 2, // Fingers
        "swan_monkfish" to 1, // Fresh monkfish
        "bowl_mushroom_fried" to 5, // Fried mushrooms
        "bowl_onion_fried" to 5, // Fried onions
        "dorgesh_frog_burger" to 2, // Frogburger
        "dorgesh_frog_spawn_gumbo" to 2, // Frogspawn gumbo
        "fruit_batta" to 11, // Fruit batta
        "aluft_fruit_batta" to 11, // Fruit batta
        "giant_carp" to 6, // Giant carp
        "giant_frog_legs" to 6, // Giant frog legs
        "raids_bat2_cooked" to 11, // Giral bat (2)
        "dorgesh_green_gloop_soup" to 2, // Green gloop soup
        "dorgesh_grubs_a_la_mode" to 2, // Grubs à la mode
        "raids_bat0_cooked" to 5, // Guanic bat (0)
        "herring" to 5, // Herring
        "toa_honey_locust" to 20, // Honey locust
        "king_worm" to 2, // King worm
        "raids_bat4_cooked" to 17, // Kryket bat (4)
        "raids_fish6_cooked" to 23, // Kyren fish (6)
        "lava_eel" to 11, // Lava eel
        "raids_fish2_cooked" to 11, // Leckish fish (2)
        "lemon" to 2, // Lemon
        "lemon_chunks" to 2, // Lemon chunks
        "lemon_slices" to 2, // Lemon slices
        "lime" to 2, // Lime
        "lime_chunks" to 2, // Lime chunks
        "lime_slices" to 2, // Lime slices
        "dorgesh_steamed_pond_loach" to 3, // Loach
        "lobster" to 12, // Lobster
        "locust_meat" to 3, // Locust meat
        "mackerel" to 6, // Mackerel
        "mantaray" to 22, // Manta ray
        "bowl_carne" to 13, // Minced meat
        "mm_monkey_bar" to 5, // Monkey bar
        "mm_monkey_nuts" to 4, // Monkey nuts
        "monkfish" to 16, // Monkfish
        "br_monkfish" to 16, // Monkfish
        "raids_bat5_cooked" to 20, // Murng bat (5)
        "bowl_mushroom+onion" to 11, // Mushroom & onion
        "potato_mushroom+onion" to 20, // Mushroom potato
        "dorgesh_sauteed_mushrooms" to 2, // Mushrooms
        "raids_fish4_cooked" to 17, // Mycil fish (4)
        "onion" to 1, // Onion
        "bowl_oniontomato" to 3, // Onion & tomato
        "orange" to 2, // Orange
        "orange_chunks" to 2, // Orange chunks
        "orange_slices" to 2, // Orange slices
        "gauntlet_food" to 20, // Paddlefish
        "papaya" to 8, // Papaya fruit
        "peach" to 8, // Peach
        "raids_bat3_cooked" to 14, // Phluxia bat (3)
        "pike" to 8, // Pike
        "pineapple_chunks" to 2, // Pineapple chunks
        "pineapple_ring" to 2, // Pineapple ring
        "pot_of_cream" to 1, // Pot of cream
        "potato" to 1, // Potato
        "potato_butter" to 14, // Potato with butter
        "potato_cheese" to 16, // Potato with cheese
        "raids_bat1_cooked" to 8, // Prael bat (1)
        "premade_cheese+tom_batta" to 11, // Premade c+t batta
        "premade_chocchip_crunchies" to 8, // Premade ch' crunch
        "premade_chocolate_bomb" to 15, // Premade choc bomb
        "premade_fruit_batta" to 11, // Premade fr't batta
        "premade_spicy_crunchies" to 7, // Premade s'y crunch
        "premade_toad_batta" to 11, // Premade t'd batta
        "premade_toad_crunchies" to 8, // Premade t'd crunch
        "premade_tangled_toads_legs" to 15, // Premade ttl
        "premade_veg_ball" to 12, // Premade veg ball
        "premade_vegetable_batta" to 11, // Premade veg batta
        "premade_worm_batta" to 11, // Premade w'm batta
        "premade_worm_crunchies" to 8, // Premade w'm crun'
        "premade_worm_hole" to 12, // Premade worm hole
        "raids_bat6_cooked" to 23, // Psykk bat (6)
        "pumpkin" to 14, // Pumpkin
        "raids_fish0_cooked" to 5, // Pysk fish (0)
        "hunting_fish_special" to 11, // Rainbow fish
        "hundred_ilm_red_banana" to 5, // Red banana
        "spit_roasted_beast_meat" to 8, // Roast beast meat
        "spit_roasted_bird_meat" to 6, // Roast bird meat
        "dorgesh_whole_roasted_frog" to 2, // Roast frog
        "spit_roasted_rabbit_meat" to 7, // Roast rabbit
        "brut_roe" to 3, // Roe
        "roll" to 6, // Roll
        "raids_fish5_cooked" to 20, // Roqed fish (5)
        "salmon" to 9, // Salmon
        "sardine" to 4, // Sardine
        "bowl_egg_scrambled" to 5, // Scrambled egg
        "seaturtle" to 21, // Sea turtle
        "shark" to 20, // Shark
        "br_shark" to 20, // Shark
        "cert_baguette" to 20, // Shark
        "shrimp" to 3, // Shrimps
        "tbwt_sliced_banana" to 2, // Sliced banana
        "hundred_ilm_sliced_red_banana" to 5, // Sliced red banana
        "spicy_crunchies" to 7, // Spicy crunchies
        "aluft_spicy_crunchies" to 7, // Spicy crunchies
        "bowl_spicymeat" to 3, // Spicy minced meat
        "bowl_chilli" to 2, // Spicy sauce
        "bowl_spicytomato" to 2, // Spicy tomato
        "spinach_roll" to 2, // Spinach roll
        "square_sandwich" to 6, // Square sandwich
        "bim_steak_sandwich" to 6, // Steak sandwich
        "stew" to 11, // Stew
        "hundred_ilm_cooked_stuffed_snake" to 20, // Stuffed snake
        "raids_fish1_cooked" to 8, // Suphi fish (1)
        "swordfish" to 14, // Swordfish
        "tangled_toads_legs" to 15, // Tangled toad's legs
        "hundred_ilm_tchiki_monkey_nuts" to 5, // Tchiki monkey nuts
        "aluft_toad_batta" to 11, // Toad batta
        "toad_batta" to 11, // Toad batta
        "aluft_toad_crunchies" to 8, // Toad crunchies
        "toad_crunchies" to 8, // Toad crunchies
        "tomato" to 2, // Tomato
        "triangle_sandwich" to 6, // Triangle sandwich
        "trout" to 7, // Trout
        "tuna" to 10, // Tuna
        "deadman_starter_tuna" to 10, // Tuna
        "bowl_tuna+sweetcorn" to 13, // Tuna and corn
        "potato_tuna+sweetcorn" to 22, // Tuna potato
        "ugthanki_kebab" to 19, // Ugthanki kebab
        "ugthanki_kebab_bad" to 19, // Ugthanki kebab
        "cooked_ugthanki_meat" to 3, // Ugthanki meat
        "aluft_veg_ball" to 12, // Veg ball
        "veg_ball" to 12, // Veg ball
        "vegetable_batta" to 11, // Vegetable batta
        "aluft_vegetable_batta" to 11, // Vegetable batta
        "garden_white_tree_fruit" to 3, // White tree fruit
        "aluft_worm_batta" to 11, // Worm batta
        "worm_batta" to 11, // Worm batta
        "worm_crunchies" to 8, // Worm crunchies
        "aluft_worm_crunchies" to 8, // Worm crunchies
        "worm_hole" to 12, // Worm hole
        "aluft_worm_hole" to 12, // Worm hole
    )
