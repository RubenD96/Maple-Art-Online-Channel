package scripting;

import client.Character;
import client.Client;
import client.inventory.item.templates.ItemTemplate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import managers.ItemManager;
import net.maple.packets.CharacterPackets;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public abstract class AbstractPlayerInteraction {

    protected @NonNull final Client c;

    public Character getPlayer() {
        return c.getCharacter();
    }

    public void gainItem(int id, int quantity) {
        if (quantity > 0) {
            ItemTemplate item = ItemManager.getItem(id);
            if (item != null) {
                CharacterPackets.modifyInventory(getPlayer(),
                        i -> i.add(item, (short) quantity),
                        false);
            }
        } else {
            CharacterPackets.modifyInventory(getPlayer(),
                    i -> i.take(id, (short) -quantity),
                    false);
        }
    }

    public void gainMeso(int meso) {
        getPlayer().gainMeso(meso);
    }

    public boolean haveItem(int id, int quantity) {
        return getPlayer().getItemQuantity(id) >= quantity;
    }

    public void gainExp(int exp) {
        getPlayer().gainExp(exp);
    }
}
