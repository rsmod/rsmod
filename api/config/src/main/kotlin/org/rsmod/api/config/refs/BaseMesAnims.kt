package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.mesanim.MesAnimBuilder

typealias mesanims = BaseMesAnims

object BaseMesAnims : MesAnimBuilder() {
    val quiz =
        build("quiz") {
            len1 = seqs.chat_quiz1
            len2 = seqs.chat_quiz2
            len3 = seqs.chat_quiz3
            len4 = seqs.chat_quiz4
        }

    val bored =
        build("bored") {
            len1 = seqs.chat_bored1
            len2 = seqs.chat_bored2
            len3 = seqs.chat_bored3
            len4 = seqs.chat_bored4
        }

    val short =
        build("short") {
            len1 = seqs.chat_short
            len2 = seqs.chat_short
            len3 = seqs.chat_short
            len4 = seqs.chat_short
        }

    val happy =
        build("happy") {
            len1 = seqs.chat_happy1
            len2 = seqs.chat_happy2
            len3 = seqs.chat_happy3
            len4 = seqs.chat_happy4
        }

    val shocked =
        build("shocked") {
            len1 = seqs.chat_happy1
            len2 = seqs.chat_happy2
            len3 = seqs.chat_happy3
            len4 = seqs.chat_happy4
        }

    val confused =
        build("confused") {
            len1 = seqs.chat_confused1
            len2 = seqs.chat_confused2
            len3 = seqs.chat_confused3
            len4 = seqs.chat_confused4
        }

    val silent =
        build("silent") {
            len1 = seqs.chat_silent
            len2 = seqs.chat_silent
            len3 = seqs.chat_silent
            len4 = seqs.chat_silent
        }

    val goblin =
        build("goblin") {
            len1 = seqs.chat_goblin1
            len2 = seqs.chat_goblin2
            len3 = seqs.chat_goblin3
            len4 = seqs.chat_goblin4
        }

    val neutral =
        build("neutral") {
            len1 = seqs.chat_neutral1
            len2 = seqs.chat_neutral2
            len3 = seqs.chat_neutral3
            len4 = seqs.chat_default
        }

    val shifty =
        build("shifty") {
            len1 = seqs.chat_shifty1
            len2 = seqs.chat_shifty2
            len3 = seqs.chat_shifty3
            len4 = seqs.chat_shifty4
        }

    val worried =
        build("worried") {
            len1 = seqs.chat_worried1
            len2 = seqs.chat_worried2
            len3 = seqs.chat_worried3
            len4 = seqs.chat_worried4
        }

    val drunk =
        build("drunk") {
            len1 = seqs.chat_drunk1
            len2 = seqs.chat_drunk2
            len3 = seqs.chat_drunk3
            len4 = seqs.chat_drunk4
        }

    val very_mad =
        build("very_mad") {
            len1 = seqs.chat_verymad
            len2 = seqs.chat_verymad
            len3 = seqs.chat_verymad
            len4 = seqs.chat_verymad
        }

    val laugh =
        build("laugh") {
            len1 = seqs.chat_laugh1
            len2 = seqs.chat_laugh2
            len3 = seqs.chat_laugh3
            len4 = seqs.chat_laugh4
        }

    val mad_laugh =
        build("mad_laugh") {
            len1 = seqs.chat_madlaugh
            len2 = seqs.chat_madlaugh
            len3 = seqs.chat_madlaugh
            len4 = seqs.chat_madlaugh
        }

    val sad =
        build("sad") {
            len1 = seqs.chat_sad1
            len2 = seqs.chat_sad2
            len3 = seqs.chat_sad3
            len4 = seqs.chat_sad4
        }

    val angry =
        build("angry") {
            len1 = seqs.chat_angry1
            len2 = seqs.chat_angry2
            len3 = seqs.chat_angry3
            len4 = seqs.chat_angry4
        }
}
