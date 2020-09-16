package managers

import client.inventory.ItemInventoryType
import client.inventory.item.templates.*

object ItemManager : AbstractManager() {

    private val items: MutableMap<Int, ItemTemplate> = HashMap()

    fun getItem(id: Int): ItemTemplate? {
        if (id < 999999) {
            return null
        }

        synchronized(items) {
            var item = items[id]
            if (item == null) {
                val type = ItemInventoryType.values()[id / 1000000 - 1]
                val subType = id % 1000000 / 10000
                val data = getData(
                        "wz/" +
                                "" + (if (type != ItemInventoryType.EQUIP) "Item" else "Equip") +
                                "/" + id + ".mao"
                )
                        ?: return null

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
                            null
                        }
                    }
                }
                if (item != null) {
                    items[id] = item
                }
            }
            return item
        }
    }
}