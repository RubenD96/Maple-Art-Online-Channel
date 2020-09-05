package client.player.friend;

import client.Character;
import net.server.Server;

public class Friend {

    private final int characterId;
    private int channel;
    private final String name;
    private String group;
    private boolean online;

    public Friend(int characterId, int channel, String name, String group, boolean online) {
        this.characterId = characterId;
        this.channel = channel;
        this.name = name;
        this.group = group;
        this.online = online;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        Character friend = Server.Companion.getInstance().getCharacter(characterId);
        return friend != null && !friend.getClient().isDisconnecting();
    }

    /*public int getChannel() {
        Character friend = Server.getInstance().getCharacter(characterId);
        return friend == null ? -1 : friend.getChannel().getChannelId();
    }*/
}