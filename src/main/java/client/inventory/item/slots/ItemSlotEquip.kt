package client.inventory.item.slots

import client.inventory.item.flags.ScrollFlag
import client.inventory.item.templates.UpgradeScrollItemTemplate
import kotlin.properties.ReadWriteProperty

class ItemSlotEquip : ItemSlot() {
    var ruc by getObservableValue<Byte>(0) // upgrades left
    var cuc by getObservableValue<Byte>(0) // upgrades used

    var str by getObservableValue<Short>(0)
    var dex by getObservableValue<Short>(0)
    var int by getObservableValue<Short>(0)
    var luk by getObservableValue<Short>(0)

    var maxHP by getObservableValue<Short>(0)
    var maxMP by getObservableValue<Short>(0)

    var pad by getObservableValue<Short>(0)
    var mad by getObservableValue<Short>(0)
    var pdd by getObservableValue<Short>(0)
    var mdd by getObservableValue<Short>(0)
    var acc by getObservableValue<Short>(0)
    var eva by getObservableValue<Short>(0)

    var craft by getObservableValue<Short>(0)
    var speed by getObservableValue<Short>(0)
    var jump by getObservableValue<Short>(0)
    var attribute by getObservableValue<Short>(0) // a flag
    var title by getObservableValue("")

    //var levelUpType by getObservableValue<Byte>(0) // ?
    var level by getObservableValue<Byte>(0) // weapon level
    var exp by getObservableValue(0) // related to weapon level
    var durability by getObservableValue(0)

    var iuc by getObservableValue(0) // hammers applied
    var grade by getObservableValue<Byte>(0) // changes border and "uniqueness" - not sure if any of it matters
    var chuc by getObservableValue<Byte>(0) // stars

    var option1 by getObservableValue<Short>(0) // potential 1
    var option2 by getObservableValue<Short>(0) // potential 2
    var option3 by getObservableValue<Short>(0) // potential 3
    //var socket1 by getObservableValue<Short>(0) // unused nebulite stat
    //var socket2 by getObservableValue<Short>(0) // unused nebulite stat

    fun applyScroll(template: UpgradeScrollItemTemplate) {
        if (template.containsFlag(ScrollFlag.INC_MHP)) maxHP = (maxHP + template.mhp).toShort()
        if (template.containsFlag(ScrollFlag.INC_MMP)) maxHP = (maxMP + template.mmp).toShort()

        if (template.containsFlag(ScrollFlag.INC_STR)) str = (str + template.str).toShort()
        if (template.containsFlag(ScrollFlag.INC_DEX)) dex = (dex + template.dex).toShort()
        if (template.containsFlag(ScrollFlag.INC_INT)) int = (int + template.int).toShort()
        if (template.containsFlag(ScrollFlag.INC_LUK)) luk = (luk + template.luk).toShort()
        if (template.containsFlag(ScrollFlag.INC_PAD)) pad = (pad + template.pad).toShort()
        if (template.containsFlag(ScrollFlag.INC_PDD)) pdd = (pdd + template.pdd).toShort()
        if (template.containsFlag(ScrollFlag.INC_MAD)) mad = (mad + template.mad).toShort()
        if (template.containsFlag(ScrollFlag.INC_MDD)) mdd = (mdd + template.mdd).toShort()
        if (template.containsFlag(ScrollFlag.INC_ACC)) acc = (acc + template.acc).toShort()
        if (template.containsFlag(ScrollFlag.INC_EVA)) eva = (eva + template.eva).toShort()

        if (template.containsFlag(ScrollFlag.INC_CRAFT)) craft = (craft + template.craft).toShort()
        if (template.containsFlag(ScrollFlag.INC_SPEED)) speed = (speed + template.speed).toShort()
        if (template.containsFlag(ScrollFlag.INC_JUMP)) jump = (jump + template.jump).toShort()

        ruc--
        cuc++
    }
}