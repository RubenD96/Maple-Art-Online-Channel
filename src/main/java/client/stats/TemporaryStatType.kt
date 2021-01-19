package client.stats

enum class TemporaryStatType(val type: Int) {
    PAD(0x0),
    PDD(0x1),
    MAD(0x2),
    MDD(0x3),
    ACC(0x4),
    EVA(0x5),
    CRAFT(0x6),
    SPEED(0x7),
    JUMP(0x8),
    MAGIC_GUARD(0x9),
    DARK_SIGHT(0xA),
    BOOSTER(0xB),
    POWER_BUARD(0xC),
    MAX_HP(0xD),
    MAX_MP(0xE),
    INVINCIBLE(0xF),
    SOUL_ARROW(0x10),
    STUN(0x11),
    POISON(0x12),
    SEAL(0x13),
    DARKNESS(0x14),
    COMBO_COUNTER(0x15),
    WEAPON_CHARGE(0x16),
    DRAGON_BLOOD(0x17),
    HOLY_SYMBOL(0x18),
    MESO_UP(0x19),
    SHADOW_PARTNER(0x1A),
    PICK_POCKET(0x1B),
    MESO_GUARD(0x1C),
    THAW(0x1D),
    WEAKNESS(0x1E),
    CURSE(0x1F),
    SLOW(0x20),
    MORPH(0x21),
    REGEN(0x22),
    BASIC_STAT_UP(0x23),
    STANCE(0x24),
    SHARP_EYES(0x25),
    MANA_REFLECTION(0x26),
    ATTRACT(0x27),
    SPIRIT_JAVELIN(0x28),
    INFINITY(0x29),
    HOLY_SHIELD(0x2A),
    HAM_STRING(0x2B),
    BLIND(0x2C),
    CONCENTRATION(0x2D),
    BAN_MAP(0x2E),
    MAX_LEVEL_BUFF(0x2F),
    MESO_UP_BY_ITEM(0x30),
    GHOST(0x31),
    BARRIER(0x32),
    REVERSE_INPUT(0x33),
    ITEM_UP_BY_ITEM(0x34),
    RESPECT_P_IMMUNE(0x35),
    RESPECT_M_IMMUNE(0x36),
    DEFENSE_ATT(0x37),
    DEFENSE_STATE(0x38),
    INC_EFFECT_HP_POTION(0x39),
    INC_EFFECT_MP_POTION(0x3A),
    DOJANG_BESERK(0x3B),
    DOJANG_INVINCIBLE(0x3C),
    SPARK(0x3D),
    DOJAN_SHIELD(0x3E),
    SOUL_MASTER_FINAL(0x3F),
    WIND_BREAKER_FINAL(0x40),
    ELEMENTAL_RESET(0x41),
    WIND_WALK(0x42),
    EVENT_RATE(0x43),
    COMBO_ABILITY_BUFF(0x44),
    COMBO_DRAIN(0x45),
    COMBO_BARRIER(0x46),
    BODY_PRESSURE(0x47),
    SMART_KNOCKBACK(0x48),
    REPEAT_EFFECT(0x49),
    EXP_BUFF_RATE(0x4A),
    STOP_PORTION(0x4B),
    STOP_MOTION(0x4C),
    FEAR(0x4D),
    EVAN_SLOW(0x4E),
    MAGIC_SHIELD(0x4F),
    MAGIC_RESISTANCE(0x50),
    SOUL_STONE(0x51),
    FLYING(0x52),
    FROZEN(0x53),
    ASSIST_CHARGE(0x54),
    ENRAGE(0x55),
    SUDDEN_DEATH(0x56),
    NOT_DAMAGED(0x57),
    FINAL_CUT(0x58),
    THORNS_EFFECT(0x59),
    SWALLOW_ATTACK_DAMAGE(0x5A),
    MOREWORLD_DAMAGE_UP(0x5B),
    MINE(0x5C),
    EMHP(0x5D),
    EMMP(0x5E),
    EPAD(0x5F),
    EPDD(0x60),
    EMDD(0x61),
    GUARD(0x62),
    SAFETY_DAMAGE(0x63),
    SAFETY_ABSORB(0x64),
    CYCLONE(0x65),
    SWALLOW_CRITICAL(0x66),
    SWALLOW_MAX_HP(0x67),
    SWALLOW_DEFENCE(0x68),
    SWALLOW_EVASION(0x69),
    CONVERSION(0x6A),
    REVIVE(0x6B),
    SNEAK(0x6C),
    MECHANIC(0x6D),
    AURA(0x6E),
    DARK_AURA(0x6F),
    BLUE_AURA(0x70),
    YELLOW_AURA(0x71),
    SUPER_BODY(0x72),
    MOREWILD_MAX_HP(0x73),
    DICE(0x74),
    BLESSING_ARMOR(0x75),
    DAM_R(0x76),
    TELEPORT_MASTERY_ON(0x77),
    COMBAT_ORDERS(0x78),
    BEHOLDER(0x79),
    ENERGY_CHARGED(0x7A),
    DASH_SPEED(0x7B),
    DASH_JUMP(0x7C),
    RIDE_VEHICLE(0x7D),
    PARTY_BOOSTER(0x7E),
    GUIDED_BULLET(0x7F),
    UNDEAD(0x80),
    SUMMON_BOMB(0x81);
}