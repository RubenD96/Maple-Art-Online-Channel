package net.database;

import client.Character;
import client.player.friend.FriendList;
import net.server.Server;
import org.jooq.Record;
import org.jooq.Result;

import static database.jooq.Tables.FRIENDS;

public class FriendAPI {

    public static void loadFriends(Character character) {
        Result<Record> friends = DatabaseCore.getConnection().select().from(FRIENDS).where(FRIENDS.CID.eq(character.getId())).fetch();

        FriendList friendList = character.getFriendList();
        for (Record record : friends) {
            int fid = record.getValue(FRIENDS.FID);
            friendList.addFriend(fid, CharacterAPI.getOfflineName(fid), record.getValue(FRIENDS.GROUP));

            Character chr = Server.getInstance().getCharacter(fid);
            if (chr != null) {
                friendList.getFriends().get(fid).setChannel(chr.getChannel().getChannelId());
            }
        }
        friendList.updateFriendList();
        friendList.notifyMutualFriends();
    }

    public static void addFriend(int cid, int fid, String group) {
        DatabaseCore.getConnection()
                .insertInto(FRIENDS,
                        FRIENDS.CID,
                        FRIENDS.FID,
                        FRIENDS.GROUP)
                .values(cid, fid, group)
                .execute();
    }

    public static void removeFriend(int cid, int fid) {
        DatabaseCore.getConnection()
                .deleteFrom(FRIENDS)
                .where(FRIENDS.CID.eq(cid))
                .and(FRIENDS.FID.eq(fid))
                .execute();
    }

    public static void updateGroup(String oldName, String newName) {
        DatabaseCore.getConnection()
                .update(FRIENDS)
                .set(FRIENDS.GROUP, newName)
                .where(FRIENDS.GROUP.eq(oldName))
                .execute();
    }
}