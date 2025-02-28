package org.rsmod.game.type.obj

public enum class WeaponCategory(public val id: Int, public val text: String) {
    Unarmed(0, "Unarmed"),
    Axe(1, "Axe"),
    Blunt(2, "Blunt"),
    Bow(3, "Bow"),
    Claw(4, "Claw"),
    Crossbow(5, "Crossbow"),
    Salamander(6, "Salamander"),
    Chinchompas(7, "Chinchompas"),
    Gun(8, "Gun"),
    SlashSword(9, "Slash Sword"),
    TwoHandedSword(10, "2h sword"),
    Pickaxe(11, "Pickaxe"),
    Polearm(12, "Polearm"),
    Polestaff(13, "Polestaff"),
    Scythe(14, "Scythe"),
    Spear(15, "Spear"),
    Spiked(16, "Spiked"),
    StabSword(17, "Stab Sword"),
    Staff(18, "Staff"),
    Thrown(19, "Thrown"),
    Whip(20, "Whip"),
    BladedStaff(21, "Bladed Staff"),
    Banner(22, "Banner"),
    PoweredStaff(24, "Powered Staff"),
    Bludgeon(27, "Bludgeon"),
    Bulwark(28, "Bulwark");

    public companion object {
        public fun getOrUnarmed(id: Int?): WeaponCategory =
            if (id == null) {
                Unarmed
            } else {
                this[id] ?: Unarmed
            }

        public operator fun get(id: Int): WeaponCategory? =
            when (id) {
                Unarmed.id -> Unarmed
                Axe.id -> Axe
                Blunt.id -> Blunt
                Bow.id -> Bow
                Claw.id -> Claw
                Crossbow.id -> Crossbow
                Salamander.id -> Salamander
                Chinchompas.id -> Chinchompas
                Gun.id -> Gun
                SlashSword.id -> SlashSword
                TwoHandedSword.id -> TwoHandedSword
                Pickaxe.id -> Pickaxe
                Polearm.id -> Polearm
                Polestaff.id -> Polestaff
                Scythe.id -> Scythe
                Spear.id -> Spear
                Spiked.id -> Spiked
                StabSword.id -> StabSword
                Staff.id -> Staff
                Thrown.id -> Thrown
                Whip.id -> Whip
                BladedStaff.id -> BladedStaff
                Banner.id -> Banner
                PoweredStaff.id -> PoweredStaff
                Bludgeon.id -> Bludgeon
                Bulwark.id -> Bulwark
                else -> null
            }
    }
}
