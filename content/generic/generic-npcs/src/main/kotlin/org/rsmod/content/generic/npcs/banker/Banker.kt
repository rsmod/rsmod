package org.rsmod.content.generic.npcs.banker

import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onApNpc1
import org.rsmod.api.script.onApNpc3
import org.rsmod.api.script.onApNpc4
import org.rsmod.api.script.onApNpcU
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.script.onOpNpc4
import org.rsmod.api.script.onOpNpcU
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.content.interfaces.bank.scripts.BankTutorialScript
import org.rsmod.game.entity.Npc
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Banker
@Inject
private constructor(
    private val spaceShop: BankSpaceShop,
    private val tutorial: BankTutorialScript,
) : PluginScript() {
    // TODO(content): Bank Tutor dialogue variation when player has a bank PIN set up.

    override fun ScriptContext.startup() {
        onApNpc4(content.banker) { apOpenCollectionBox(it.npc) }
        onOpNpc4(content.banker) { openCollectionBox() }
        onApNpc3(content.banker) { apOpenBank(it.npc) }
        onOpNpc3(content.banker) { openBank() }
        onApNpc1(content.banker) { apTalkToBanker(it.npc) }
        onOpNpc1(content.banker) { talkToBanker(it.npc) }
        onApNpcU(content.banker) { apBanknote(it.npc, it.invSlot, it.objType) }
        onOpNpcU(content.banker) { banknote(it.npc, it.invSlot, it.objType) }

        onApNpc4(content.banker_tutor) { apOpenCollectionBox(it.npc) }
        onOpNpc4(content.banker_tutor) { openCollectionBox() }
        onApNpc3(content.banker_tutor) { apOpenBank(it.npc) }
        onOpNpc3(content.banker_tutor) { openBank() }
        onApNpc1(content.banker_tutor) { apTalkToBanker(it.npc) }
        onOpNpc1(content.banker_tutor) { talkToBanker(it.npc) }
        onApNpcU(content.banker_tutor) { apBanknote(it.npc, it.invSlot, it.objType) }
        onOpNpcU(content.banker_tutor) { banknote(it.npc, it.invSlot, it.objType) }

        spaceShop.startup()
    }

    private fun ProtectedAccess.apOpenCollectionBox(npc: Npc) {
        if (isWithinApRange(npc, distance = 2)) {
            openCollectionBox()
        }
    }

    private fun ProtectedAccess.apOpenBank(npc: Npc) {
        if (isWithinApRange(npc, distance = 2)) {
            openBank()
        }
    }

    private fun ProtectedAccess.openBank() {
        ifOpenMainSidePair(main = interfaces.bank_main, side = interfaces.bank_side)
    }

    private suspend fun ProtectedAccess.apTalkToBanker(npc: Npc) {
        if (isWithinApRange(npc, distance = 2)) {
            talkToBanker(npc)
        }
    }

    private suspend fun ProtectedAccess.talkToBanker(npc: Npc) {
        startDialogue(npc, faceFar = true) {
            if (npc.type.isContentType(content.banker_tutor)) {
                talkToBankerTutor()
            } else {
                talkToBanker()
            }
        }
    }

    private suspend fun Dialogue.talkToBanker() {
        chatNpc(quiz, "Good day, how may I help you?")

        val blocks = access.vars[banker_varbits.blocks_purchased]
        if (spaceShop.hasPurchasedAll(blocks)) {
            talkToBankerPurchasedAllSlots()
            return
        }

        val option =
            choice5(
                "I'd like to access my bank account, please.",
                1,
                "I'd like to check my PIN settings.",
                2,
                "I'd like to collect items.",
                3,
                "I'd like to buy more bank slots.",
                4,
                "What is this place?",
                5,
            )
        when (option) {
            1 -> access.openBank()
            2 -> access.openPin()
            3 -> access.openCollectionBox()
            4 -> {
                chatPlayer(quiz, "I'd like to buy more bank slots.")
                buyBankSlots()
            }
            5 -> whatIsThisPlace()
        }
    }

    private suspend fun Dialogue.talkToBankerPurchasedAllSlots() {
        val option =
            choice4(
                "I'd like to access my bank account, please.",
                1,
                "I'd like to check my PIN settings.",
                2,
                "I'd like to collect items.",
                3,
                "What is this place?",
                4,
            )
        when (option) {
            1 -> access.openBank()
            2 -> access.openPin()
            3 -> access.openCollectionBox()
            4 -> whatIsThisPlace()
        }
    }

    private suspend fun Dialogue.talkToBankerTutor() {
        chatNpc(quiz, "Good day, how may I help you?")

        val blocks = access.vars[banker_varbits.blocks_purchased]
        if (spaceShop.hasPurchasedAll(blocks)) {
            talkToBankerTutorPurchasedAllSlots()
            return
        }

        val option =
            choice5(
                "How do I use the bank?",
                1,
                "I'd like to access my bank account, please.",
                2,
                "I'd like to check my PIN settings.",
                3,
                "I'd like to collect items.",
                4,
                "I'd like to buy more bank slots.",
                5,
            )
        when (option) {
            1 -> howToUseBank()
            2 -> access.openBank()
            3 -> access.openPin()
            4 -> access.openCollectionBox()
            5 -> {
                chatPlayer(quiz, "I'd like to buy more bank slots.")
                buyBankSlots()
            }
        }
    }

    private suspend fun Dialogue.talkToBankerTutorPurchasedAllSlots() {
        val option =
            choice5(
                "How do I use the bank?",
                1,
                "I'd like to access my bank account, please.",
                2,
                "I'd like to check my PIN settings.",
                3,
                "I'd like to collect items.",
                4,
                "What is this place?",
                5,
            )
        when (option) {
            1 -> howToUseBank()
            2 -> access.openBank()
            3 -> access.openPin()
            4 -> access.openCollectionBox()
            5 -> whatIsThisPlace()
        }
    }

    private fun ProtectedAccess.openPin() {
        ifOpenMainModal(interfaces.bankpin_settings)
    }

    private fun ProtectedAccess.openCollectionBox() {
        ifOpenMainModal(interfaces.ge_collection_box)
    }

    private suspend fun Dialogue.buyBankSlots() {
        // TODO(content): Check if player is ultimate ironman. If so, give dialogue and return
        //  early.

        val blocks = access.vars[banker_varbits.blocks_purchased]
        val costs = spaceShop.listCosts(blocks)
        if (costs.isEmpty()) {
            // Note: Not sure if this is allowed or if the option to buy more bank slots is
            // completely removed once you have purchased them all. Either way, this dialogue
            // is not official.
            chatNpc(quiz, "I can't sell you any more bank slots.")
            return
        }
        val slotsLeft = costs.size * SLOTS_PER_BLOCK

        chatNpc(
            happy,
            "I can sell you up to $slotsLeft additional bank slots in sets of " +
                "$SLOTS_PER_BLOCK. How many are you interested in buying?",
        )

        val mappedCosts = costs.mapIndexed { index, cost -> (index + 1) * SLOTS_PER_BLOCK to cost }
        val choices =
            mappedCosts.map { (slots, cost) -> "$slots slots (${cost.formatAmount} coins)" }
        val selection =
            access.menu(
                "How many do you wish to purchase?",
                *choices.toTypedArray(),
                "Do I have any other options for extra bank space?",
                "(Cancel)",
            )
        val cancelSelection = mappedCosts.size + 1
        val otherOptionsSelection = cancelSelection - 1

        val mappedCost = mappedCosts.getOrNull(selection)
        if (mappedCost != null) {
            val (slots, cost) = mappedCost
            val block = selection + 1

            chatPlayer(happy, "$slots slots please.")
            chatNpc(
                happy,
                "Buying $slots additional bank slots will cost ${cost.formatAmount} " +
                    "coins. Be warned, this purchase is not reversible. Are " +
                    "you happy to proceed?",
            )

            if (access.invCoinTotal() < cost) {
                chatPlayer(
                    confused,
                    "Oh... I don't seem to have enough money for that. Never mind.",
                )
                return
            }

            val confirmation =
                choice2(
                    "Yes.",
                    true,
                    "No.",
                    false,
                    "Buy $slots bank slots for ${cost.formatAmount} coins?",
                )

            if (!confirmation) {
                chatPlayer(confused, "Actually, I've changed my mind.")
                return
            }

            chatPlayer(happy, "Yes, I'm happy with that.")

            val newCapacity = access.vars[varbits.bank_capacity] + slots
            check(newCapacity > slots) { "`bank_capacity` should have been previously assigned." }

            val takeFee = access.invTakeFee(cost)
            if (!takeFee) {
                chatPlayer(
                    confused,
                    "Oh... I don't seem to have enough money for that. Never mind.",
                )
                return
            }

            access.vars[banker_varbits.blocks_purchased] += block
            access.soundSynth(synths.coins_jingle_1)
            access.vars[varbits.bank_capacity] = min(newCapacity, access.bank.size)
            chatNpc(happy, "Your additional bank slots have been added.")
            return
        }

        if (selection == cancelSelection) {
            chatPlayer(confused, "Actually, I've changed my mind.")
            return
        }

        if (selection == otherOptionsSelection) {
            chatPlayer(quiz, "Do I have any other options for extra bank space?")
            chatNpc(
                shifty,
                "I'm not supposed to tell you this, but you can obtain " +
                    "more bank space by setting up a PIN.",
            )
            buyBankSlots()
            return
        }
    }

    private suspend fun Dialogue.whatIsThisPlace() {
        chatPlayer(quiz, "What is this place?")
        chatNpc(happy, "This is a branch of the Bank of Gielinor. We have branches in many towns.")
        chatPlayer(quiz, "And what do you do?")
        chatNpc(
            happy,
            "We will look after your items and money for you. " +
                "Leave your valuables with us if you want to keep them " +
                "safe.",
        )
    }

    private suspend fun Dialogue.howToUseBank() {
        val option =
            choice5(
                "Using the bank itself.",
                1,
                "Using Bank deposit boxes.",
                2,
                "What's this PIN thing that people keep talking about?",
                3,
                "Can you show me the bank tutorial please?",
                4,
                "Goodbye.",
                5,
            )
        when (option) {
            1 -> usingBankItself()
            2 -> usingDepositBoxes()
            3 -> whatsABankPinExtended()
            4 -> showTutorial()
            5 -> goodbye()
        }
    }

    private suspend fun Dialogue.usingBankItself() {
        chatPlayer(quiz, "Using the bank itself. I'm not sure how....?")
        chatNpc(
            happy,
            "To open your bank you can speak to any banker, as " +
                "well as use a bank booth or bank chest. If you have a " +
                "PIN setup you will be asked to enter the PIN before " +
                "you are given access to your bank.",
        )
        val option = choice2("What's a bank PIN?", 1, "Continue.", 2)
        if (option == 1) {
            whatsABankPin()
        } else if (option == 2) {
            usingBankItselfContinue()
        }
    }

    private suspend fun Dialogue.whatsABankPin() {
        chatPlayer(quiz, "What's a bank PIN?")
        chatNpc(
            happy,
            "The PIN - Personal Identification Number - can be " +
                "set on your bank account to protect your items in case " +
                "someone finds out your account password. It consists " +
                "of four numbers that you remember and tell no one.",
        )
        chatNpc(
            happy,
            "So if someone did manage to get your password they " +
                "couldn't steal your items if they were in the bank.",
        )
        chatPlayer(quiz, "Ok, so after I am in the bank, how do I use it?")
        usingBankItselfContinue()
    }

    private suspend fun Dialogue.usingBankItselfContinue() {
        chatNpc(
            happy,
            "To withdraw one item, left-click on it once. To withdraw " +
                "many, right-click on the item and select from the menu. " +
                "The same can be done for depositing items.",
        )
        chatNpc(
            happy,
            "While you are in your bank you can click and drag " +
                "items to move them around the bank. There are two " +
                "modes for moving items, Swap or Insert.",
        )
        chatNpc(
            happy,
            "If you are using swap, the two items will switch place. " +
                "If you are using Insert, the item you are moving will " +
                "be placed either in front or behind the item you " +
                "targeted with the item you are moving.",
        )
        chatNpc(
            happy,
            "You may withdraw 'notes' or 'certificates'. This will only " +
                "work for items which are tradable and do not stack. To " +
                "withdraw an Item as note, you need to select the 'note' " +
                "withdraw as button.",
        )
        doubleobjbox(
            objs.shrimps,
            400,
            ocCert(objs.shrimps),
            400,
            "A noted item looks like a piece of paper with the image " +
                "of the actual item on top of it.",
        )
        chatNpc(
            happy,
            "You can use bank notes on any banker to un-note the " +
                "item. Alternatively, you can deposit the items into the " +
                "bank. Then withdraw them as an item instead of a note.",
        )
        howToUseBank()
    }

    private suspend fun Dialogue.usingDepositBoxes() {
        chatPlayer(quiz, "Using Bank deposit boxes.... what are they?")
        chatNpc(
            happy,
            "They look like grey pillars, there's one just over there, " +
                "near the desk. You can usually find a Bank deposit box " +
                "next to a bank.",
        )
        chatNpc(
            happy,
            " Bank deposit boxes save so much time as you do not " +
                "have to enter in your bank PIN. If you're simply " +
                "wanting to deposit a single item, 'Use' it on the deposit " +
                "box.",
        )
        chatNpc(
            happy,
            "Otherwise, simply click once on the box and it will give " +
                "you a choice of what to deposit in an interface very " +
                "similar to the bank itself. Very quick for when you're " +
                "simply fishing or mining etc.",
        )
        howToUseBank()
    }

    private suspend fun Dialogue.whatsABankPinExtended() {
        chatPlayer(quiz, "What's this PIN thing that people keep talking about?")
        chatNpc(
            happy,
            "The PIN - Personal Identification Number - can be " +
                "set on your bank account to protect your items in case " +
                "someone finds out your account password. It consists " +
                "of four numbers that you remember and tell no one.",
        )
        chatNpc(
            happy,
            "So if someone did manage to get your password they " +
                "couldn't steal your items if they were in the bank.",
        )
        bankPinExtendedOptions()
    }

    private suspend fun Dialogue.bankPinExtendedOptions() {
        val option =
            choice5(
                "How do I set my PIN?",
                1,
                "How do I remove my PIN?",
                2,
                "What happens if I forget my PIN?",
                3,
                "I know about the PIN, tell me about the bank.",
                4,
                "Goodbye.",
                5,
            )
        when (option) {
            1 -> howToSetPin()
            2 -> howToRemovePin()
            3 -> howToRecoverPin()
            4 -> howToUseBank()
            5 -> goodbye()
        }
    }

    private suspend fun Dialogue.howToSetPin() {
        chatPlayer(quiz, "How do I set my PIN?")
        chatNpc(
            happy,
            "You can set your PIN by talking to any banker, they " +
                "will allow you to access your bank pin settings. Here " +
                "you can choose to set your pin and recovery delay.",
        )
        chatNpc(
            happy,
            "Remember not to set it to anything personal such as " +
                "your real life bank PIN or birthday. The recovery " +
                "delay is to protect your banked items from account " +
                "thieves.",
        )
        chatNpc(
            happy,
            "If someone stole your account and asked to have the " +
                "PIN deleted, they would have to wait a few days before " +
                "accessing your bank account to steal your items. This " +
                "will give you time to recover your account.",
        )
        chatNpc(
            happy,
            "There will also be a delay in actually setting the PIN " +
                "to be used, this is so that if your account is stolen and " +
                "a PIN set, you can cancel it before it comes into use!",
        )
        chatNpc(quiz, "Would you like to setup a bank pin?")

        val setPin =
            choice2(
                "Yes please.",
                true,
                "No thanks.",
                false,
                title = "Would you like to setup a bank pin?",
            )

        if (setPin) {
            chatPlayer(neutral, "Yes please.")
            access.openPin()
        } else {
            chatPlayer(neutral, "No thanks.")
            bankPinExtendedOptions()
        }
    }

    private suspend fun Dialogue.howToRemovePin() {
        chatPlayer(quiz, "How do I remove my PIN?")
        chatNpc(
            happy,
            "Talking to any banker will enable you to access your " +
                "PIN settings. There you can cancel or change your " +
                "PIN, but you will need to wait for your recovery " +
                "delay to expire to be able to access your bank.",
        )
        chatNpc(
            happy,
            "This can be set in the settings page and will protect " +
                "your items should your account be stolen.",
        )
        bankPinExtendedOptions()
    }

    private suspend fun Dialogue.howToRecoverPin() {
        chatPlayer(quiz, "What happens if I forget my PIN?")
        chatNpc(
            happy,
            "If you find yourself faced with the PIN keypad and " +
                "you don't know the PIN, just look on the right-hand " +
                "side for a button marked 'I don't know it'. Click this " +
                "button. Your PIN will be deleted (after a delay of a few days) " +
                "and you'll be able to use your bank as before. You " +
                "may still use the bank deposit box without your PIN.",
        )
        bankPinExtendedOptions()
    }

    private suspend fun Dialogue.showTutorial() {
        tutorial.begin(access)
        access.ifClose()
        howToUseBank()
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private suspend fun ProtectedAccess.apBanknote(
        npc: Npc,
        invSlot: Int,
        objType: UnpackedObjType,
    ) {
        if (isWithinApRange(npc, 3)) {
            banknote(npc, invSlot, objType)
        }
    }

    private suspend fun ProtectedAccess.banknote(npc: Npc, invSlot: Int, objType: UnpackedObjType) {
        if (!objType.isCert) {
            startDialogue(npc) {
                chatNpcNoTurn(sad, "Hand me a banknote, and I'll try to convert it to an item.")
            }
            return
        }

        if (inv.isFull()) {
            startDialogue(npc) { chatNpcNoTurn(sad, "You don't have any inventory space.") }
            return
        }

        startDialogue(npc) {
            val confirmation = choice2("Yes", true, "No", false, "Un-note the banknote?")
            if (!confirmation) {
                return@startDialogue
            }

            val invObj = inv[invSlot]
            check(invObj.isType(objType)) {
                "Unexpected `invObj` when un-certifying! (found=$invObj, expectedType=$objType)"
            }

            val count = min(inv.freeSpace(), invObj.count)
            if (count == 0) {
                chatNpcNoTurn(sad, "You don't have any inventory space.")
                return@startDialogue
            }

            val uncert = ocUncert(objType)
            val replace = invReplace(inv, invSlot, count, uncert)
            if (replace.success) {
                objbox(uncert, 400, "The bank exchanges your banknote for an item.")
            }
        }
    }

    private companion object {
        private const val SLOTS_PER_BLOCK = 40
    }
}

private class BankSpaceShop @Inject constructor(private val enumResolver: EnumTypeMapResolver) {
    private lateinit var blockCosts: List<Int>

    fun startup() {
        val costs = enumResolver[banker_enums.block_costs].filterValuesNotNull()
        val maxBlock = costs.keys.maxOrNull() ?: error("`block_costs` enum should not be empty.")
        val blockCosts = MutableList(maxBlock) { 0 }
        for ((block, cost) in costs) {
            blockCosts[block - 1] = cost
        }
        this.blockCosts = blockCosts
    }

    fun listCosts(purchasedBlocks: Int): List<Int> {
        if (purchasedBlocks >= blockCosts.size) {
            return emptyList()
        }
        val blocks = blockCosts.drop(purchasedBlocks)
        return blocks.runningReduce { cumulative, cost -> cumulative + cost }
    }

    fun hasPurchasedAll(purchasedBlocks: Int): Boolean {
        return purchasedBlocks >= blockCosts.size
    }
}
