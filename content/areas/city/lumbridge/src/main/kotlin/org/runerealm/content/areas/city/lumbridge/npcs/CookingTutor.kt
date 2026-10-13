package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Cordero, the Cooking tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Cooking_tutor">Transcript</a>
 */
class CookingTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.cooking_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) {
            chatPlayer(happy, "Hello.")
            chatNpc(happy, "Hello there! Are you interested in hearing all about cooking?")
            mainMenu()
        }
    }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice3(
                "How can I train my cooking?",
                1,
                "What kinds of things can I cook?",
                2,
                "No, thank you.",
                3,
            )
        when (choice) {
            1 -> trainCooking()
            2 -> foodTypes()
            3 -> noThanks()
        }
    }

    private suspend fun Dialogue.trainCooking() {
        chatPlayer(quiz, "How can I train my cooking?")
        chatNpc(
            neutral,
            "The simplest thing to cook is raw meat or fish. Shrimp " +
                "can be caught south of here, to the east of Lumbridge " +
                "Swamp. You could ask the fishing tutor for a net if you " +
                "have not got one.",
        )
        chatNpc(
            neutral,
            "Alternatively, you could acquire some raw beef or " +
                "chicken. You can find cows and chickens north of here. " +
                "Go over the bridge then follow the path north, you can't " +
                "miss them!",
        )
        objbox(
            CookingTutorObjs.range_icon,
            "When you have a full inventory of meat or fish, find a " +
                "range. Look for this icon on your minimap.",
        )
        chatNpc(happy, "You could use my range here if you like.")
        chatNpc(
            neutral,
            "If you wanted to, my friend the cook in Lumbridge Castle " +
                "has a range you could use. You might find you burn even " +
                "less on that range as its top notch.",
        )
        chatNpc(
            neutral,
            "Alternatively, you can use your own fire, but it's not as " +
                "effective and the food will burn more frequently. To " +
                "make a fire, use a tinderbox on some logs.",
        )
        chatNpc(
            neutral,
            "Once you've found your range or fire, use your raw food " +
                "on it. This will bring up a menu of the food you can " +
                "cook. Then select the food you want.",
        )
        chatNpc(
            neutral,
            "When you have a full inventory of cooked food, drop the " +
                "useless burnt food and find a bank.",
        )
        objbox(
            objs.bank_icon,
            "Look for this symbol on your minimap after climbing the " +
                "stairs of the Lumbridge Castle to the top. There are " +
                "numerous banks around the world, all marked with that " +
                "symbol.",
        )
        if (vars[varps.cookquest] < COOKS_ASSISTANT_COMPLETE) {
            chatNpc(
                happy,
                "If you're interested in quests, I heard my friend the cook " +
                    "in Lumbridge Castle is in need of a hand. Just talk to " +
                    "him and he'll set you off.",
            )
        }
        chatNpc(happy, "Is there anything else you want to know?")
        mainMenu()
    }

    private suspend fun Dialogue.foodTypes() {
        chatPlayer(quiz, "What kinds of things can I cook?")
        chatNpc(
            happy,
            "Many things! You can cook anything from fish to poultry. " +
                "You could try your hand at making baked potatoes or " +
                "even brew some of the good stuff.",
        )
        chatNpc(happy, "What would you like to hear about?")
        foodMenu()
    }

    private suspend fun Dialogue.foodMenu() {
        val choice =
            choice4(
                "Tell me about Fish and Meat.",
                1,
                "Tell me about Brewing.",
                2,
                "Tell me about Vegetables.",
                3,
                "Tell me about Pies and Pizzas.",
                4,
            )
        when (choice) {
            1 -> fishAndMeat()
            2 -> brewing()
            3 -> vegetables()
            4 -> piesAndPizzas()
        }
    }

    private suspend fun Dialogue.anythingElseMentioned() {
        chatNpc(happy, "Would you like to hear about anything else I mentioned?")
        foodMenu()
    }

    private suspend fun Dialogue.fishAndMeat() {
        chatPlayer(quiz, "Tell me about Fish and Meat.")
        doubleobjbox(
            CookingTutorObjs.raw_beef,
            CookingTutorObjs.cooked_meat,
            "Fish and meat of most varieties can be cooked very " +
                "simply on either a fire or range, experiment which one " +
                "works for you.",
        )
        chatNpc(
            neutral,
            "You might find that when killing monsters, they drop " +
                "some raw meat for you to cook.",
        )
        chatNpc(
            neutral,
            "Some foods have hidden properties. Anglerfish can " +
                "provide you with an overheal effect where you have a " +
                "few more hitpoints than your level. The higher your " +
                "hitpoints the better the boost.",
        )
        chatNpc(
            neutral,
            "Most of the time, cooking meat and fish requires nothing " +
                "else but a range or fire. More complicated dishes might " +
                "require specialized equipment.",
        )
        anythingElseMentioned()
    }

    private suspend fun Dialogue.brewing() {
        chatPlayer(quiz, "Tell me about Brewing.")
        doubleobjbox(
            CookingTutorObjs.dragon_bitter,
            CookingTutorObjs.beer,
            "You can brew your own beers using the fermenting vats in " +
                "either Keldagrim or Port Phasmatys.",
        )
        chatNpc(
            neutral,
            "Use two buckets of water, two handfuls of barley malt, 4 " +
                "hops of your choice and a pot of ale yeast, then leave " +
                "to ferment for a few days!",
        )
        chatNpc(
            neutral,
            "Simply turn the tap and let the ale flow into the barrel " +
                "next to the vat, then use empty beer glasses on the " +
                "barrel.",
        )
        anythingElseMentioned()
    }

    private suspend fun Dialogue.vegetables() {
        chatPlayer(quiz, "Tell me about Vegetables.")
        doubleobjbox(
            CookingTutorObjs.potato,
            CookingTutorObjs.tuna_potato,
            "Baked potatoes are excellent foods and they are healthy too!",
        )
        chatNpc(
            neutral,
            "You'll need to churn some butter and cheese. Look for the " +
                "churn icon on the minimap. There is a churn in the farm " +
                "building northwest of Lumbridge.",
        )
        doubleobjbox(
            CookingTutorObjs.pat_of_butter,
            CookingTutorObjs.cheese,
            "The nearest dairy cow is north of the nearby water mill. " +
                "Take buckets of milk with you to the churn and use the " +
                "milk on the churn to make butter or cheese.",
        )
        chatNpc(
            neutral,
            "Now you will need to pick your topping if you want one. " +
                "Get yourself a bowel and create the perfect topping. " +
                "Then put the topping onto the potato.",
        )
        anythingElseMentioned()
    }

    private suspend fun Dialogue.piesAndPizzas() {
        chatPlayer(quiz, "Tell me about Pies and Pizzas.")
        doubleobjbox(
            CookingTutorObjs.bucket_of_water,
            CookingTutorObjs.pot_of_flour,
            "Use a pot of flour with a bucket of water. You will then " +
                "need to make some pastry dough.",
        )
        doubleobjbox(
            CookingTutorObjs.pie_dish,
            CookingTutorObjs.pastry_dough,
            "Use the pastry dough with a pie dish to get a pie shell, " +
                "then add your choice of filling such as apples or red " +
                "berries.",
        )
        chatNpc(
            happy,
            "Finally cook your pie by using the unbaked pie on a cooking range. Mmmm...pie.",
        )
        chatNpc(
            neutral,
            "Some pies might even be able to boost some of your " + "skills, be it temporarily.",
        )
        chatNpc(
            neutral,
            "Next, we have pizza. You will first need to make yourself " +
                "a pizza base, use a pot of flour with a bucket of water " +
                "and make a pizza base.",
        )
        doubleobjbox(
            CookingTutorObjs.tomato,
            CookingTutorObjs.cheese,
            "Now you will need to get yourself a tomato and some " +
                "cheese. Use the tomato on the pizza base and then use " +
                "the cheese. Cook the uncooked pizza base and there you " +
                "have yourself a plain pizza.",
        )
        chatNpc(
            happy,
            "If you want to, you could add yourself a topping to the " +
                "pizza too. Why not try some anchovies?",
        )
        anythingElseMentioned()
    }

    private suspend fun Dialogue.noThanks() {
        chatPlayer(neutral, "No, thank you.")
        chatNpc(happy, "Well, just come back any time you want to know how to cook up a storm!")
    }

    private companion object {
        const val COOKS_ASSISTANT_COMPLETE = 2
    }
}

internal object CookingTutorObjs : ObjReferences() {
    val range_icon = find("range_icon_dummy")
    val raw_beef = find("raw_beef")
    val cooked_meat = find("cooked_meat")
    val dragon_bitter = find("dragon_bitter")
    val beer = find("beer")
    val potato = find("potato")
    val tuna_potato = find("potato_tuna+sweetcorn")
    val pat_of_butter = find("pot_of_butter")
    val cheese = find("cheese")
    val bucket_of_water = find("bucket_water")
    val pot_of_flour = find("pot_flour")
    val pie_dish = find("piedish")
    val pastry_dough = find("pastry_dough")
    val tomato = find("tomato")
}
