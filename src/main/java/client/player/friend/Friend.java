package client.player.friend;

import client.Character;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.server.Server;

@Getter
@RequiredArgsConstructor
public class Friend {

    private @NonNull int characterId;
    private @NonNull @Setter int channel;
    private @NonNull String name;
    private @NonNull @Setter String group;
    private @NonNull @Setter boolean online;

    public boolean isOnline() {
        Character friend = Server.getInstance().getCharacter(characterId);
        return friend != null && !friend.getClient().isDisconnecting();
    }

    /*public int getChannel() {
        Character friend = Server.getInstance().getCharacter(characterId);
        return friend == null ? -1 : friend.getChannel().getChannelId();
    }*/
}