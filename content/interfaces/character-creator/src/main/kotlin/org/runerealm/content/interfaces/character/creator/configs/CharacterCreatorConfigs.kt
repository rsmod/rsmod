package org.runerealm.content.interfaces.character.creator.configs

import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.api.type.refs.varp.VarpReferences

typealias creator_interfaces = CharacterCreatorInterfaces

typealias creator_components = CharacterCreatorComponents

typealias creator_varbits = CharacterCreatorVarBits

typealias creator_varps = CharacterCreatorVarps

object CharacterCreatorInterfaces : InterfaceReferences() {
    val player_design = find("player_design")
}

object CharacterCreatorComponents : ComponentReferences() {
    val head_left = find("player_design:head_left")
    val head_right = find("player_design:head_right")
    val jaw_left = find("player_design:jaw_left")
    val jaw_right = find("player_design:jaw_right")
    val torso_left = find("player_design:torso_left")
    val torso_right = find("player_design:torso_right")
    val arms_left = find("player_design:arms_left")
    val arms_right = find("player_design:arms_right")
    val hands_left = find("player_design:hands_left")
    val hands_right = find("player_design:hands_right")
    val legs_left = find("player_design:legs_left")
    val legs_right = find("player_design:legs_right")
    val feet_left = find("player_design:feet_left")
    val feet_right = find("player_design:feet_right")

    val hair_left = find("player_design:hair_left")
    val hair_right = find("player_design:hair_right")
    val torso_col_left = find("player_design:torso_col_left")
    val torso_col_right = find("player_design:torso_col_right")
    val legs_col_left = find("player_design:legs_col_left")
    val legs_col_right = find("player_design:legs_col_right")
    val feet_col_left = find("player_design:feet_col_left")
    val feet_col_right = find("player_design:feet_col_right")
    val skin_left = find("player_design:skin_left")
    val skin_right = find("player_design:skin_right")

    val body_type_a = find("player_design:gender_male")
    val body_type_b = find("player_design:gender_female")
    val pronoun_options = find("player_design:pronouns_buttons")
    val confirm = find("player_design:confirm")
}

object CharacterCreatorVarBits : VarBitReferences() {
    val body_type = find("player_design_bodytype")
    val pronouns = find("settings_transmit_pronouns")
}

object CharacterCreatorVarps : VarpReferences() {
    val tutorial = find("tutorial")
}
