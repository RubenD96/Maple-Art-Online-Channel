package client.player

enum class WeaponType(val type: Int) {

    WEAPONLESS(0),
    SWORD_1H(1),
    SWORD_2H(2),
    MACE_1H(3),
    MACE_2H(4),
    AXE_1H(5),
    AXE_2H(6),
    DAGGER(7),
    KNUCKLE(8),
    SPEAR(9),
    POLEARM(10),
    THROWING_KNIVE(11);

    companion object {

        fun getById(id: Int) : WeaponType {
            values().forEach {
                if (it.type == id) return it
            }
            return WEAPONLESS
        }
    }
}