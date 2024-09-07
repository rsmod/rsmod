package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.mesanim.MesAnimBuilder
import org.rsmod.game.type.mesanim.MesAnimType

public typealias mesanims = BaseMesAnims

public object BaseMesAnims : MesAnimBuilder() {
    public val quiz: MesAnimType =
        build("quiz") {
            len1 = seqs.chat_quiz1
            len2 = seqs.chat_quiz2
            len3 = seqs.chat_quiz3
            len4 = seqs.chat_quiz4
        }

    public val bored: MesAnimType =
        build("bored") {
            len1 = seqs.chat_bored1
            len2 = seqs.chat_bored2
            len3 = seqs.chat_bored3
            len4 = seqs.chat_bored4
        }

    public val short: MesAnimType =
        build("short") {
            len1 = seqs.chat_short
            len2 = seqs.chat_short
            len3 = seqs.chat_short
            len4 = seqs.chat_short
        }

    public val happy: MesAnimType =
        build("happy") {
            len1 = seqs.chat_happy1
            len2 = seqs.chat_happy2
            len3 = seqs.chat_happy3
            len4 = seqs.chat_happy4
        }

    public val shocked: MesAnimType =
        build("shocked") {
            len1 = seqs.chat_happy1
            len2 = seqs.chat_happy2
            len3 = seqs.chat_happy3
            len4 = seqs.chat_happy4
        }

    public val confused: MesAnimType =
        build("confused") {
            len1 = seqs.chat_confused1
            len2 = seqs.chat_confused2
            len3 = seqs.chat_confused3
            len4 = seqs.chat_confused4
        }

    public val silent: MesAnimType =
        build("silent") {
            len1 = seqs.chat_silent
            len2 = seqs.chat_silent
            len3 = seqs.chat_silent
            len4 = seqs.chat_silent
        }

    public val goblin: MesAnimType =
        build("goblin") {
            len1 = seqs.chat_goblin1
            len2 = seqs.chat_goblin2
            len3 = seqs.chat_goblin3
            len4 = seqs.chat_goblin4
        }

    public val neutral: MesAnimType =
        build("neutral") {
            len1 = seqs.chat_neutral1
            len2 = seqs.chat_neutral2
            len3 = seqs.chat_neutral3
            len4 = seqs.chat_default
        }

    public val shifty: MesAnimType =
        build("shifty") {
            len1 = seqs.chat_shifty1
            len2 = seqs.chat_shifty2
            len3 = seqs.chat_shifty3
            len4 = seqs.chat_shifty4
        }

    public val worried: MesAnimType =
        build("worried") {
            len1 = seqs.chat_worried1
            len2 = seqs.chat_worried2
            len3 = seqs.chat_worried3
            len4 = seqs.chat_worried4
        }

    public val drunk: MesAnimType =
        build("drunk") {
            len1 = seqs.chat_drunk1
            len2 = seqs.chat_drunk2
            len3 = seqs.chat_drunk3
            len4 = seqs.chat_drunk4
        }

    public val very_mad: MesAnimType =
        build("very_mad") {
            len1 = seqs.chat_verymad
            len2 = seqs.chat_verymad
            len3 = seqs.chat_verymad
            len4 = seqs.chat_verymad
        }

    public val laugh: MesAnimType =
        build("laugh") {
            len1 = seqs.chat_laugh1
            len2 = seqs.chat_laugh2
            len3 = seqs.chat_laugh3
            len4 = seqs.chat_laugh4
        }

    public val mad_laugh: MesAnimType =
        build("mad_laugh") {
            len1 = seqs.chat_madlaugh
            len2 = seqs.chat_madlaugh
            len3 = seqs.chat_madlaugh
            len4 = seqs.chat_madlaugh
        }

    public val sad: MesAnimType =
        build("sad") {
            len1 = seqs.chat_sad1
            len2 = seqs.chat_sad2
            len3 = seqs.chat_sad3
            len4 = seqs.chat_sad4
        }

    public val angry: MesAnimType =
        build("angry") {
            len1 = seqs.chat_angry1
            len2 = seqs.chat_angry2
            len3 = seqs.chat_angry3
            len4 = seqs.chat_angry4
        }
}
