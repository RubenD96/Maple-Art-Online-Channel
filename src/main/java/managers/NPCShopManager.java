package managers;

import client.interaction.shop.NPCShop;

import java.util.HashMap;
import java.util.Map;

public class NPCShopManager {

    private final Map<Integer, NPCShop> shops;
    private static final NPCShopManager instance = new NPCShopManager();

    public static NPCShopManager getInstance() {
        return instance;
    }

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
