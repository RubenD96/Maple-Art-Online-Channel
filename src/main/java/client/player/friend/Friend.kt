package client.player.friend

import net.server.Server.getCharacter

class Friend(val characterId: Int, var channel: Int, val name: String, var group: String, private var online: Boolean) {

    fun setOnline(online: Boolean) {
        this.online = online
    }

    fun isOnline(): Boolean {
        val friend = getCharacter(characterId)
        return friend != null && !friend.client.isDisconnecting
    }

    /*public int getChannel() {
        Character friend = Server.getInstance().getCharacter(characterId);
        return friend == null ? -1 : friend.getChannel().getChannelId();
    }*/
}