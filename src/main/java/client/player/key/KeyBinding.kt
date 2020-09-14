package client.player.key

class KeyBinding(val type: Byte, val action: Int) {

    companion object {
        val defaultBindings = arrayOfNulls<KeyBinding>(90)

        init {
            defaultBindings[2] = KeyBinding(KeyType.MENU, KeyAction.CHAT_ALL)
            defaultBindings[3] = KeyBinding(KeyType.MENU, KeyAction.CHAT_PARTY)
            defaultBindings[4] = KeyBinding(KeyType.MENU, KeyAction.CHAT_FRIEND)
            defaultBindings[5] = KeyBinding(KeyType.MENU, KeyAction.CHAT_GUILD)
            defaultBindings[6] = KeyBinding(KeyType.MENU, KeyAction.CHAT_ALLIANCE)
            defaultBindings[7] = KeyBinding(KeyType.MENU, KeyAction.CHAT_COUPLE)
            defaultBindings[8] = KeyBinding(KeyType.MENU, KeyAction.CHAT_EXPEDITION)
            defaultBindings[16] = KeyBinding(KeyType.MENU, KeyAction.QUEST)
            defaultBindings[17] = KeyBinding(KeyType.MENU, KeyAction.WORLD_MAP)
            defaultBindings[18] = KeyBinding(KeyType.MENU, KeyAction.EQUIP)
            defaultBindings[19] = KeyBinding(KeyType.MENU, KeyAction.FRIEND)
            defaultBindings[20] = KeyBinding(KeyType.MENU, KeyAction.EXPEDITION)
            defaultBindings[23] = KeyBinding(KeyType.MENU, KeyAction.ITEM)
            defaultBindings[24] = KeyBinding(KeyType.MENU, KeyAction.PARTY_SEARCH)
            defaultBindings[25] = KeyBinding(KeyType.MENU, KeyAction.PARTY)
            defaultBindings[26] = KeyBinding(KeyType.MENU, KeyAction.SHORTCUT)
            defaultBindings[27] = KeyBinding(KeyType.MENU, KeyAction.QUICK_SLOT)
            defaultBindings[29] = KeyBinding(KeyType.BASIC_ACTION, KeyAction.ATTACK)
            defaultBindings[31] = KeyBinding(KeyType.MENU, KeyAction.STAT)
            defaultBindings[33] = KeyBinding(KeyType.MENU, KeyAction.FAMILY)
            defaultBindings[34] = KeyBinding(KeyType.MENU, KeyAction.GUILD)
            defaultBindings[35] = KeyBinding(KeyType.MENU, KeyAction.CHAT_WHISPER)
            defaultBindings[37] = KeyBinding(KeyType.MENU, KeyAction.SKILL)
            defaultBindings[38] = KeyBinding(KeyType.MENU, KeyAction.QUEST_ALARM)
            defaultBindings[39] = KeyBinding(KeyType.MENU, KeyAction.MEDAL_QUEST)
            defaultBindings[40] = KeyBinding(KeyType.MENU, KeyAction.CHAT_TYPE)
            defaultBindings[41] = KeyBinding(KeyType.MENU, KeyAction.CASH_SHOP)
            defaultBindings[43] = KeyBinding(KeyType.MENU, KeyAction.KEY_CONFIG)
            defaultBindings[44] = KeyBinding(KeyType.BASIC_ACTION, KeyAction.PICKUP)
            defaultBindings[45] = KeyBinding(KeyType.BASIC_ACTION, KeyAction.SIT)
            defaultBindings[46] = KeyBinding(KeyType.MENU, KeyAction.MESSENGER)
            defaultBindings[50] = KeyBinding(KeyType.MENU, KeyAction.MINI_MAP)
            defaultBindings[56] = KeyBinding(KeyType.BASIC_ACTION, KeyAction.JUMP)
            defaultBindings[57] = KeyBinding(KeyType.BASIC_ACTION, KeyAction.NPC_TALK)
            defaultBindings[59] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_0)
            defaultBindings[60] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_1)
            defaultBindings[61] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_2)
            defaultBindings[62] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_3)
            defaultBindings[63] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_4)
            defaultBindings[64] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_5)
            defaultBindings[65] = KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_6)
        }
    }
}