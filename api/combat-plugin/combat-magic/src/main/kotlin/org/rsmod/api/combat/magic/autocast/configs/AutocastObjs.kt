package org.rsmod.api.combat.magic.autocast.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.game.type.obj.ObjType

internal object AutocastObjEditor : ObjEditor() {
    init {
        addAutocast(objs.skull_sceptre_i, objs.spell_crumble_undead)
        addAutocast(objs.ibans_staff, objs.spell_iban_blast)
        addAutocast(objs.ibans_staff_u, objs.spell_iban_blast)
        addAutocast(objs.slayers_staff, objs.spell_crumble_undead, objs.spell_magic_dart)
        addAutocast(objs.slayers_staff_e, objs.spell_crumble_undead, objs.spell_magic_dart)
        addAutocast(objs.thammarons_sceptre_a, objs.spell_flames_of_zamorak)
        addAutocast(objs.accursed_sceptre_a, objs.spell_flames_of_zamorak)

        addAutocast(
            objs.staff_of_the_dead,
            objs.spell_crumble_undead,
            objs.spell_magic_dart,
            objs.spell_flames_of_zamorak,
        )

        addAutocast(
            objs.toxic_staff_of_the_dead,
            objs.spell_crumble_undead,
            objs.spell_magic_dart,
            objs.spell_flames_of_zamorak,
        )

        addAutocast(
            objs.staff_of_light,
            objs.spell_crumble_undead,
            objs.spell_magic_dart,
            objs.spell_saradomin_strike,
        )

        addAutocast(
            objs.staff_of_balance,
            objs.spell_crumble_undead,
            objs.spell_magic_dart,
            objs.spell_claws_of_guthix,
        )

        addAutocast(objs.void_knight_mace, objs.spell_crumble_undead, objs.spell_claws_of_guthix)
        addAutocast(objs.void_knight_mace_l, objs.spell_crumble_undead, objs.spell_claws_of_guthix)
    }

    private fun addAutocast(staff: ObjType, spell: ObjType) {
        edit(staff.internalNameValue) { param[autocast_params.additional_spell_autocast1] = spell }
    }

    private fun addAutocast(staff: ObjType, spell1: ObjType, spell2: ObjType) {
        edit(staff.internalNameValue) {
            param[autocast_params.additional_spell_autocast1] = spell1
            param[autocast_params.additional_spell_autocast2] = spell2
        }
    }

    private fun addAutocast(staff: ObjType, spell1: ObjType, spell2: ObjType, spell3: ObjType) {
        edit(staff.internalNameValue) {
            param[autocast_params.additional_spell_autocast1] = spell1
            param[autocast_params.additional_spell_autocast2] = spell2
            param[autocast_params.additional_spell_autocast3] = spell3
        }
    }
}
