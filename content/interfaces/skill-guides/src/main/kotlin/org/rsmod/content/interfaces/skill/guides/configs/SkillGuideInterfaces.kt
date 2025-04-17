package org.rsmod.content.interfaces.skill.guides.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias guide_interfaces = SkillGuideInterfaces

typealias guide_components = SkillGuideComponents

object SkillGuideInterfaces : InterfaceReferences() {
    val skill_guide = find("skill_guide", 9223372035487308313)
}

object SkillGuideComponents : ComponentReferences() {
    val attack = find("skills_tab:attack", 8620294563512592028)
    val strength = find("skills_tab:strength", 4308224042835635792)
    val defence = find("skills_tab:defence", 8186724404162415658)
    val ranged = find("skills_tab:ranged", 3026972490850285874)
    val prayer = find("skills_tab:prayer", 1777386256324157983)
    val magic = find("skills_tab:magic", 8754290082204056967)
    val runecraft = find("skills_tab:runecraft", 5439101537975849664)
    val construction = find("skills_tab:construction", 8099680982235723666)
    val hitpoints = find("skills_tab:hitpoints", 4217971940356618047)
    val agility = find("skills_tab:agility", 4001186860681529862)
    val herblore = find("skills_tab:herblore", 8912488376859349434)
    val thieving = find("skills_tab:thieving", 6815220749698047995)
    val crafting = find("skills_tab:crafting", 2503150229021091759)
    val fletching = find("skills_tab:fletching", 8411333721647660264)
    val slayer = find("skills_tab:slayer", 6678226557278515445)
    val hunter = find("skills_tab:hunter", 3548157775266174300)
    val mining = find("skills_tab:mining", 8006220199204380168)
    val smithing = find("skills_tab:smithing", 3694149678527423932)
    val fishing = find("skills_tab:fishing", 6390648301189588707)
    val cooking = find("skills_tab:cooking", 6508183567543942065)
    val firemaking = find("skills_tab:firemaking", 9204682190206106840)
    val woodcutting = find("skills_tab:woodcutting", 4892611669529150604)
    val farming = find("skills_tab:farming", 6556309137340275673)

    val subsection_1 = find("skill_guide:00", 3963436522595488001)
    val subsection_2 = find("skill_guide:01", 2667562805622441783)
    val subsection_3 = find("skill_guide:02", 1371689088649395565)
    val subsection_4 = find("skill_guide:03", 75815371676349347)
    val subsection_5 = find("skill_guide:04", 8003313691558078937)
    val subsection_6 = find("skill_guide:05", 6707439974585032719)
    val subsection_7 = find("skill_guide:06", 5411566257611986501)
    val subsection_8 = find("skill_guide:07", 4115692540638940283)
    val subsection_9 = find("skill_guide:08", 2819818823665894065)
    val subsection_10 = find("skill_guide:09", 1523945106692847847)
    val subsection_11 = find("skill_guide:10", 228071389719801629)
    val subsection_12 = find("skill_guide:11", 8155569709601531219)
    val subsection_13 = find("skill_guide:12", 6859695992628485001)
    val subsection_14 = find("skill_guide:13", 5563822275655438783)
    val subsection_entry_list = find("skill_guide:icons", 384452582875284890)
    val close_button = find("skill_guide:close", 1811048218123348417)
}
