package client.inventory.item.templates

import client.inventory.item.flags.ScrollFlag
import util.packet.PacketReader

class UpgradeScrollItemTemplate(id: Int) : ItemBundleTemplate(id) {

    private var scrollFlags: Int = 0

    var mhp = 0
    var mmp = 0

    var str: Short = 0
    var dex: Short = 0
    var int: Short = 0
    var luk: Short = 0
    var pad: Short = 0
    var pdd: Short = 0
    var mad: Short = 0
    var mdd: Short = 0
    var acc: Short = 0
    var eva: Short = 0

    var craft: Short = 0
    var speed: Byte = 0
    var jump: Byte = 0

    var category: Byte = 0
    var success: Byte = 0
    var cursed: Byte = 0

    override fun decode(r: PacketReader): UpgradeScrollItemTemplate {
        super.decode(r)

        scrollFlags = r.readInteger()
        if (containsFlag(ScrollFlag.INC_MHP)) mhp = r.readInteger()
        if (containsFlag(ScrollFlag.INC_MMP)) mmp = r.readInteger()

        if (containsFlag(ScrollFlag.INC_STR)) str = r.readShort()
        if (containsFlag(ScrollFlag.INC_DEX)) dex = r.readShort()
        if (containsFlag(ScrollFlag.INC_INT)) int = r.readShort()
        if (containsFlag(ScrollFlag.INC_LUK)) luk = r.readShort()
        if (containsFlag(ScrollFlag.INC_PAD)) pad = r.readShort()
        if (containsFlag(ScrollFlag.INC_PDD)) pdd = r.readShort()
        if (containsFlag(ScrollFlag.INC_MAD)) mad = r.readShort()
        if (containsFlag(ScrollFlag.INC_MDD)) mdd = r.readShort()
        if (containsFlag(ScrollFlag.INC_ACC)) acc = r.readShort()
        if (containsFlag(ScrollFlag.INC_EVA)) eva = r.readShort()

        if (containsFlag(ScrollFlag.INC_CRAFT)) craft = r.readShort()
        if (containsFlag(ScrollFlag.INC_SPEED)) speed = r.readByte()
        if (containsFlag(ScrollFlag.INC_JUMP)) jump = r.readByte()

        if (containsFlag(ScrollFlag.ENCHANT_CATEGORY)) category = r.readByte()
        if (containsFlag(ScrollFlag.SUCCESS)) success = r.readByte()
        if (containsFlag(ScrollFlag.CURSED)) cursed = r.readByte()

        return this
    }

    fun containsFlag(flag: ScrollFlag): Boolean {
        return scrollFlags and flag.value == flag.value
    }
}