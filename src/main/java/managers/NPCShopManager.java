package managers;

import client.shop.NPCShop;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class NPCShopManager {

    private final Map<Integer, NPCShop> shops;
    private static @Getter final NPCShopManager instance = new NPCShopManager();

    private NPCShopManager() {
        shops = new HashMap<>();
    }

    public synchronized NPCShop getShop(int id) {
        NPCShop shop = shops.get(id);
        if (shop == null) {
            shop = new NPCShop(id);
            shops.put(id, shop);
        }
        return shop;
    }

    public void reload() {
        shops.clear();
    }
}
