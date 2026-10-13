package org.runerealm.api.combat.commons.fx

import org.runerealm.api.combat.commons.CombatStance
import org.runerealm.api.config.aliases.ParamSeq
import org.runerealm.api.config.aliases.ParamSynth
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.synths
import org.runerealm.game.type.seq.SeqType
import org.runerealm.game.type.synth.SynthType

public data class MeleeAnimationAndSound(
    val animParam: ParamSeq,
    val soundParam: ParamSynth,
    val defaultAnim: SeqType,
    val defaultSound: SynthType,
) {
    public companion object {
        private val stance1 =
            MeleeAnimationAndSound(
                animParam = params.attack_anim_stance1,
                soundParam = params.attack_sound_stance1,
                defaultAnim = seqs.human_unarmedpunch,
                defaultSound = synths.human_unarmedpunch,
            )

        private val stance2 =
            MeleeAnimationAndSound(
                animParam = params.attack_anim_stance2,
                soundParam = params.attack_sound_stance2,
                defaultAnim = seqs.human_unarmedkick,
                defaultSound = synths.human_unarmedkick,
            )

        private val stance3 =
            MeleeAnimationAndSound(
                animParam = params.attack_anim_stance3,
                soundParam = params.attack_sound_stance3,
                defaultAnim = seqs.human_unarmedpunch,
                defaultSound = synths.human_unarmedpunch,
            )

        private val stance4 =
            MeleeAnimationAndSound(
                animParam = params.attack_anim_stance4,
                soundParam = params.attack_sound_stance4,
                defaultAnim = seqs.human_unarmedpunch,
                defaultSound = synths.human_unarmedpunch,
            )

        public fun from(stance: CombatStance?): MeleeAnimationAndSound =
            when (stance) {
                CombatStance.Stance1 -> stance1
                CombatStance.Stance2 -> stance2
                CombatStance.Stance3 -> stance3
                CombatStance.Stance4 -> stance4
                else -> stance1
            }
    }
}
