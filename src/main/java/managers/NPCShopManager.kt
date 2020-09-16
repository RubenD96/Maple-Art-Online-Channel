package managers

import client.interaction.shop.NPCShop

object NPCShopManager {

    private val shops: MutableMap<Int, NPCShop> = HashMap()

    fun getShop(id: Int): NPCShop {
        synchronized(shops) {
            var shop = shops[id]
            if (shop == null) {
                shop = NPCShop(id)
                shops[id] = shop
            }
            return shop
        }
    }

    fun reload() {
        synchronized(shops) {
            shops.clear()
        }
    }
}