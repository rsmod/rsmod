@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.mesanim.MesAnimBuilder

typealias mesanims = BaseMesAnims

object BaseMesAnims : MesAnimBuilder() {
    val quiz =
        build("quiz") {
            len1 = seqs.chatquiz1
            len2 = seqs.chatquiz2
            len3 = seqs.chatquiz3
            len4 = seqs.chatquiz4
        }

    val bored =
        build("bored") {
            len1 = seqs.chatbored1
            len2 = seqs.chatbored2
            len3 = seqs.chatbored3
            len4 = seqs.chatbored4
        }

    val short =
        build("short") {
            len1 = seqs.shortchatneu1
            len2 = seqs.shortchatneu1
            len3 = seqs.shortchatneu1
            len4 = seqs.shortchatneu1
        }

    val happy =
        build("happy") {
            len1 = seqs.chathap1
            len2 = seqs.chathap2
            len3 = seqs.chathap3
            len4 = seqs.chathap4
        }

    val shocked =
        build("shocked") {
            len1 = seqs.chathap1
            len2 = seqs.chathap2
            len3 = seqs.chathap3
            len4 = seqs.chathap4
        }

    val confused =
        build("confused") {
            len1 = seqs.chatcon1
            len2 = seqs.chatcon2
            len3 = seqs.chatcon3
            len4 = seqs.chatcon4
        }

    val silent =
        build("silent") {
            len1 = seqs.chatidleneu1
            len2 = seqs.chatidleneu1
            len3 = seqs.chatidleneu1
            len4 = seqs.chatidleneu1
        }

    val goblin =
        build("goblin") {
            len1 = seqs.chatgoblin1
            len2 = seqs.chatgoblin2
            len3 = seqs.chatgoblin3
            len4 = seqs.chatgoblin4
        }

    val neutral =
        build("neutral") {
            len1 = seqs.chatneu1
            len2 = seqs.chatneu2
            len3 = seqs.chatneu3
            len4 = seqs.chatneu4
        }

    val shifty =
        build("shifty") {
            len1 = seqs.chatshifty1
            len2 = seqs.chatshifty2
            len3 = seqs.chatshifty3
            len4 = seqs.chatshifty4
        }

    val worried =
        build("worried") {
            len1 = seqs.chatscared1
            len2 = seqs.chatscared2
            len3 = seqs.chatscared3
            len4 = seqs.chatscared4
        }

    val drunk =
        build("drunk") {
            len1 = seqs.chatdrunk1
            len2 = seqs.chatdrunk2
            len3 = seqs.chatdrunk3
            len4 = seqs.chatdrunk4
        }

    val very_mad =
        build("very_mad") {
            len1 = seqs.evilidle1
            len2 = seqs.evilidle1
            len3 = seqs.evilidle1
            len4 = seqs.evilidle1
        }

    val laugh =
        build("laugh") {
            len1 = seqs.chatlaugh1
            len2 = seqs.chatlaugh2
            len3 = seqs.chatlaugh3
            len4 = seqs.chatlaugh4
        }

    val mad_laugh =
        build("mad_laugh") {
            len1 = seqs.evillaugh1
            len2 = seqs.evillaugh1
            len3 = seqs.evillaugh1
            len4 = seqs.evillaugh1
        }

    val sad =
        build("sad") {
            len1 = seqs.chatsad1
            len2 = seqs.chatsad2
            len3 = seqs.chatsad3
            len4 = seqs.chatsad4
        }

    val angry =
        build("angry") {
            len1 = seqs.chatang1
            len2 = seqs.chatang2
            len3 = seqs.chatang3
            len4 = seqs.chatang4
        }
}
