package client.player

import util.logging.LogType
import util.logging.Logger.log
import util.packet.IntegerValue

enum class Job(override val value: Int, val type: WeaponType) : IntegerValue {

    WEAPONLESS(0, WeaponType.WEAPONLESS),
    SWORD_1H1(100, WeaponType.SWORD_1H),
    SWORD_1H2(110, WeaponType.SWORD_1H),
    SWORD_1H3(111, WeaponType.SWORD_1H),
    SWORD_1H4(112, WeaponType.SWORD_1H),
    SWORD_2H1(200, WeaponType.SWORD_2H),
    SWORD_2H2(210, WeaponType.SWORD_2H),
    SWORD_2H3(211, WeaponType.SWORD_2H),
    SWORD_2H4(212, WeaponType.SWORD_2H),
    SPEAR1(300, WeaponType.SPEAR),
    SPEAR2(310, WeaponType.SPEAR),
    SPEAR3(311, WeaponType.SPEAR),
    SPEAR4(312, WeaponType.SPEAR),
    DAGGER1(400, WeaponType.DAGGER),
    DAGGER2(420, WeaponType.DAGGER),
    DAGGER3(421, WeaponType.DAGGER),
    DAGGER4(422, WeaponType.DAGGER),
    KNUCKLE1(500, WeaponType.KNUCKLE),
    KNUCKLE2(510, WeaponType.KNUCKLE),
    KNUCKLE3(511, WeaponType.KNUCKLE),
    KNUCKLE4(512, WeaponType.KNUCKLE);

    val id = value

    companion object {
        fun getById(id: Int): Job {
            return values().firstOrNull { it.id == id } ?: run {
                log(LogType.NULL, "Job $id does not exist, defaulting to WEAPONLESS(0)", this)
                WEAPONLESS
            }
        }
    }
}