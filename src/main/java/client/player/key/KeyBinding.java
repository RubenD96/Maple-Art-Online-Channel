package client.player.key;

public class KeyBinding {
    private final byte type;
    private final int action;

    public KeyBinding(byte type, int action) {
        this.type = type;
        this.action = action;
    }

    private static final KeyBinding[] defaultBindings = new KeyBinding[90];

    public byte getType() {
        return type;
    }

    public int getAction() {
        return action;
    }

    public static KeyBinding[] getDefaultBindings() {
        return defaultBindings;
    }

    static {
        defaultBindings[2] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_ALL);
        defaultBindings[3] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_PARTY);
        defaultBindings[4] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_FRIEND);
        defaultBindings[5] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_GUILD);
        defaultBindings[6] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_ALLIANCE);
        defaultBindings[7] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_COUPLE);
        defaultBindings[8] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_EXPEDITION);
        defaultBindings[16] = new KeyBinding(KeyType.MENU, KeyAction.QUEST);
        defaultBindings[17] = new KeyBinding(KeyType.MENU, KeyAction.WORLD_MAP);
        defaultBindings[18] = new KeyBinding(KeyType.MENU, KeyAction.EQUIP);
        defaultBindings[19] = new KeyBinding(KeyType.MENU, KeyAction.FRIEND);
        defaultBindings[20] = new KeyBinding(KeyType.MENU, KeyAction.EXPEDITION);
        defaultBindings[23] = new KeyBinding(KeyType.MENU, KeyAction.ITEM);
        defaultBindings[24] = new KeyBinding(KeyType.MENU, KeyAction.PARTY_SEARCH);
        defaultBindings[25] = new KeyBinding(KeyType.MENU, KeyAction.PARTY);
        defaultBindings[26] = new KeyBinding(KeyType.MENU, KeyAction.SHORTCUT);
        defaultBindings[27] = new KeyBinding(KeyType.MENU, KeyAction.QUICK_SLOT);
        defaultBindings[29] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.ATTACK);
        defaultBindings[31] = new KeyBinding(KeyType.MENU, KeyAction.STAT);
        defaultBindings[33] = new KeyBinding(KeyType.MENU, KeyAction.FAMILY);
        defaultBindings[34] = new KeyBinding(KeyType.MENU, KeyAction.GUILD);
        defaultBindings[35] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_WHISPER);
        defaultBindings[37] = new KeyBinding(KeyType.MENU, KeyAction.SKILL);
        defaultBindings[38] = new KeyBinding(KeyType.MENU, KeyAction.QUEST_ALARM);
        defaultBindings[39] = new KeyBinding(KeyType.MENU, KeyAction.MEDAL_QUEST);
        defaultBindings[40] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_TYPE);
        defaultBindings[41] = new KeyBinding(KeyType.MENU, KeyAction.CASH_SHOP);
        defaultBindings[43] = new KeyBinding(KeyType.MENU, KeyAction.KEY_CONFIG);
        defaultBindings[44] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.PICKUP);
        defaultBindings[45] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.SIT);
        defaultBindings[46] = new KeyBinding(KeyType.MENU, KeyAction.MESSENGER);
        defaultBindings[50] = new KeyBinding(KeyType.MENU, KeyAction.MINI_MAP);
        defaultBindings[56] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.JUMP);
        defaultBindings[57] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.NPC_TALK);
        defaultBindings[59] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_0);
        defaultBindings[60] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_1);
        defaultBindings[61] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_2);
        defaultBindings[62] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_3);
        defaultBindings[63] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_4);
        defaultBindings[64] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_5);
        defaultBindings[65] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_6);
    }
}
