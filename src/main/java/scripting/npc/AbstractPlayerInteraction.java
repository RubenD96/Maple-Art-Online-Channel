package scripting.npc;

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
public class AbstractPlayerInteraction {

    @NonNull final Client c;

    public Character getPlayer() {
        return c.getCharacter();
    }

    public void gainItem(int id, int quantity) {
        ItemTemplate item = ItemManager.getItem(id);
        if (item != null) {
            CharacterPackets.modifyInventory(getPlayer(),
                    i -> i.add(item, (short) quantity),
                    false);
        }
    }

    public void gainMeso(int meso) {
        getPlayer().gainMeso(meso);
    }
}
