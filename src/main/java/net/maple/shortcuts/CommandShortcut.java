package net.maple.shortcuts;

import client.Character;
import client.Client;
import client.inventory.item.templates.ItemTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import managers.ItemManager;
import net.maple.packets.CharacterPackets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.maple.handlers.user.UserChatHandler.refreshCommandList;

public class CommandShortcut {
    @Getter @NonNull Client c;
    @Getter @NonNull Character chr;
    @Getter @Setter String[] args;

    public CommandShortcut(Client _c, String[] _args) {
        c = _c;
        chr = c.getCharacter();
        args = _args;
    }

    public void gainMeso(int m) {
        chr.gainMeso(m);
    }

    public void reloadScripts(String script) {
        switch(script.toUpperCase()) {
            case "COMMANDS":
                refreshCommandList();
                break;
            default:
                break;
        }
    }

    public ItemTemplate getItemTemplate(int id) {
        ItemTemplate item = ItemManager.getItem(id);

        return item;
    }

    public void addItem(int id, final int qty) {
        ItemTemplate item = ItemManager.getItem(id);
        if (item != null) {
            CharacterPackets.modifyInventory(chr,
                    i -> i.add(item, (short) qty),
                    false);
        }
    }

}
