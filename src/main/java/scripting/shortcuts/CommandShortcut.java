package scripting.shortcuts;

import client.Character;
import client.Client;
import client.inventory.ItemVariationType;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import field.object.drop.EnterType;
import field.object.drop.ItemDrop;
import field.object.life.FieldMob;
import field.object.life.FieldNPC;
import lombok.Getter;
import lombok.Setter;
import managers.ItemManager;
import managers.MobManager;
import managers.NPCManager;
import managers.NPCShopManager;
import net.database.ShopAPI;
import net.maple.packets.CharacterPackets;
import net.server.Server;
import scripting.AbstractPlayerInteraction;

import static net.maple.handlers.user.UserChatHandler.refreshCommandList;

public class CommandShortcut extends AbstractPlayerInteraction {

    @Getter Client c;
    @Getter Character chr;
    @Getter @Setter String[] args;

    public CommandShortcut(Client c, String[] _args) {
        super(c);
        chr = c.getCharacter();
        args = _args;
    }

    public void gainMeso(int m) {
        chr.gainMeso(m);
    }

    public void reloadScripts(String script) {
        switch (script.toUpperCase()) {
            case "COMMANDS":
                refreshCommandList();
                break;
            default:
                break;
        }
    }

    public FieldNPC getNpc(int id) {
        return NPCManager.getNPC(id);
    }

    public FieldMob getMob(int id) {
        FieldMob mob = new FieldMob(MobManager.getMob(id), false);
        mob.setHp(mob.getTemplate().getMaxHP());
        mob.setMp(mob.getTemplate().getMaxMP());

        return mob;
    }

    public ItemTemplate getItemTemplate(int id) {
        ItemTemplate item = ItemManager.getItem(id);

        return item;
    }

    public void dropItem(int id, final int qty) {
        ItemSlot it = getItemTemplate(id).toItemSlot();

        if (it instanceof ItemSlotBundle) {
            ((ItemSlotBundle) it).setNumber((short) qty);
            ((ItemSlotBundle) it).setTitle(chr.getName());
        }

        ItemDrop drop = new ItemDrop(chr.getId(), chr, it, 0);
        drop.setPosition(chr.getPosition());
        chr.getField().enter(drop);
    }

    public void reloadShops() {
        Server.getInstance().setShops(ShopAPI.getShops());
        NPCShopManager.getInstance().reload();
    }

    public void addItem(int id, final int qty) {
        ItemTemplate item = ItemManager.getItem(id);
        if (item != null) {
            CharacterPackets.modifyInventory(chr,
                    i -> i.add(item, (short) qty),
                    false);
        }
    }

    public void reloadMap() {
        chr.getChannel().getFieldManager().reloadField(chr.getFieldId());
    }

    public void kickMe() {
        chr.getClient().close();
    }
}
