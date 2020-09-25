package managers

import client.inventory.ItemInventoryType
import client.inventory.item.templates.*
import util.logging.LogType
import util.logging.Logger
import util.logging.Logger.log

object ItemManager : AbstractManager() {

    private const val fallback = 4000000

    // assertion test to check if the fallback items exist:
    // (4000000, blue snail shell)
    // (1302000, sword) - don't think we need this
    init {
        getData("wz/Item/$fallback.mao")!!
        getData("wz/Equip/1302000.mao")!!
    }

    private val items: MutableMap<Int, ItemTemplate> = HashMap()

    fun getItem(id: Int): ItemTemplate {
        if (id < 999999) {
            log(LogType.MISSING, "item $id does not exist", this)
            return getItem(fallback)
        }

        synchronized(items) {
            var item = items[id]
            if (item == null) {
                val type = ItemInventoryType.values()[id / 1000000 - 1]
                val subType = id % 1000000 / 10000
                val data = getData("wz/" +
                        "" + (if (type != ItemInventoryType.EQUIP) "Item" else "Equip") +
                        "/" + id + ".mao")
                        ?: return run {
                            log(LogType.MISSING, "item $id does not exist", this)
                            getItem(fallback)
                        }

                item = when (type) {
                    ItemInventoryType.EQUIP -> ItemEquipTemplate(id, data)
                    ItemInventoryType.CONSUME -> when (subType) {
                        0, 1, 2, 5, 21, 36, 38, 45 -> StatChangeItemTemplate(id, data)
                        3 -> PortalScrollItemTemplate(id, data)
                        10 -> MobSummonItemTemplate(id, data)
                        else -> ItemBundleTemplate(id, data)
                    }
                    ItemInventoryType.INSTALL,
                    ItemInventoryType.ETC,
                    ItemInventoryType.CASH -> {
                        if (type != ItemInventoryType.CASH || subType != 0) {
                            ItemBundleTemplate(id, data)
                        } else { // todo pets
                            log(LogType.INVALID, "Pets are not implemented yet", this)
                            getItem(fallback)
                        }
                    }
                }
                items[id] = item
            }
            return item
        }
    }
}