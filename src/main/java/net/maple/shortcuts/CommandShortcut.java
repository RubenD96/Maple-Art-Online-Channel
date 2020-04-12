package net.maple.shortcuts;

import client.Character;
import client.Client;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static net.maple.handlers.user.UserChatHandler.refreshCommandList;

public class CommandShortcut {
    @Getter @NonNull Client c;
    @Getter @NonNull Character chr;
    @Getter @Setter List<String> arg = new ArrayList<>()

    public CommandShortcut(Client _c, List<String> _arg) {
        c = _c;
        chr = c.getCharacter();
        arg = _arg;
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

}
