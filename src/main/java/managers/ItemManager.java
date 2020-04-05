package managers;

import client.inventory.ItemInventoryType;
import client.inventory.item.templates.*;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class ItemManager extends AbstractManager {

    private static Map<Integer, ItemTemplate> items = new HashMap<>();

    public static synchronized ItemTemplate getItem(int id) {
        if (id < 999999) {
            return null;
        }
        ItemTemplate item = items.get(id);
        if (item == null) {
            ItemInventoryType type = ItemInventoryType.values()[(id / 1000000) - 1];
            int subType = id % 1000000 / 10000;
            PacketReader data = getData("wz/Item/" + id + ".mao");

            switch (type) {
                case EQUIP:
                    // todo
                    break;
                case CONSUME:
                    switch (subType) {
                        case 0:
                        case 1:
                        case 2:
                        case 5:
                        case 21:
                        case 36:
                        case 38:
                        case 45:
                            item = new StatChangeItemTemplate(id, data);
                            break;
                        case 3:
                            item = new PortalScrollItemTemplate(id, data);
                            break;
                        case 10:
                            item = new MobSummonItemTemplate(id, data);
                            break;
                        default:
                            item = new ItemBundleTemplate(id, data);
                            break;
                    }
                    break;
                case INSTALL:
                case ETC:
                case CASH:
                    if (type == ItemInventoryType.CASH && subType == 0) { // todo pets
                        break;
                    }
                    item = new ItemBundleTemplate(id, data);
                    break;
            }
            if (item != null) {
                items.put(id, item);
            }
        }
        return item;
    }
}
