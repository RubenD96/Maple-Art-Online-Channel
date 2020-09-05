package client.effects;

public enum EffectType {

    LEVEL_UP(0x00),
    SKILL_USE(0x01),
    SKILL_AFFECTED(0x02),
    SKILL_AFFECTED_SELECT(0x03),
    SKILL_SPECIAL_AFFECTED(0x04),
    QUEST(0x05),
    PET(0x06),
    SKILL_SPECIAL(0x07),
    PROTECT_ON_DIE_ITEM_USE(0x08),
    PLAY_PORTAL_SE(0x09),
    JOB_CHANGED(0x0A),
    QUEST_COMPLETE(0x0B),
    INC_DEC_HP_EFFECT(0x0C),
    BUFF_ITEM_EFFECT(0x0D),
    SQUIB_EFFECT(0x0E),
    MONSTER_BOOK_CARD_GET(0x0F),
    LOTTERY_USE(0x10),
    ITEM_LEVEL_UP(0x11),
    ITEM_MAKER(0x12),
    EXP_ITEM_CONSUMED(0x13),
    RESERVED_EFFECT(0x14),
    BUFF(0x15),
    CONSUME_EFFECT(0x16),
    UPGRADE_TOMB_ITEM_USE(0x17),
    BATTLEFIELD_ITEM_USE(0x18),
    AVATAR_ORIENTED(0x19),
    INCUBATOR_USE(0x1A),
    PLAY_SOUND_WITH_MUTE_BGM(0x1B),
    SOUL_STONE_USE(0x1C),
    INC_DEC_HP_EFFECT_EX(0x1D),
    DELIVERY_QUEST_ITEM_USE(0x1E),
    REPEAT_EFFECT_REMOVE(0x1F),
    EVOL_RING(0x20);

    private final int value;

    EffectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}