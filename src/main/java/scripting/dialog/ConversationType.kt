package scripting.dialog

enum class ConversationType(val value: Int) {
    SAY(0x00),
    ASK_YES_NO(0x02),
    ASK_TEXT(0x03),
    ASK_NUMBER(0x04),
    ASK_MENU(0x05),
    ASK_ACCEPT(0x0D),
    ASK_BOX_TEXT(0x0E),
    ASK_SLIDE_MENU(0x0F);
}