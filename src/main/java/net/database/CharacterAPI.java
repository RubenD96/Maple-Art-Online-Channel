package net.database;

import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import client.Character;
import client.Client;
import client.player.Job;

import java.util.HashMap;
import java.util.Map;

import static database.jooq.Tables.CHARACTERS;
import static database.jooq.Tables.INVENTORIES;

public class CharacterAPI {

    public static Character getNewCharacter(Client c, int id) {
        Record record = DatabaseCore.getConnection().select().from(CHARACTERS).where(CHARACTERS.ID.eq(id)).fetchOne();

        String name = record.getValue(CHARACTERS.NAME);
        int charId = record.getValue(CHARACTERS.ID);
        int gmLevel = record.getValue(CHARACTERS.GM_LEVEL);
        int level = record.getValue(CHARACTERS.LEVEL);
        int face = record.getValue(CHARACTERS.FACE);
        int hair = record.getValue(CHARACTERS.HAIR);
        int gender = record.getValue(CHARACTERS.GENDER);
        int skinColor = record.getValue(CHARACTERS.SKIN);
        Job job = Job.getById(record.getValue(CHARACTERS.JOB));
        int ap = record.getValue(CHARACTERS.AP);
        int sp = record.getValue(CHARACTERS.SP);
        int fame = record.getValue(CHARACTERS.FAME);
        int mapId = record.getValue(CHARACTERS.MAP);
        int spawnpoint = record.getValue(CHARACTERS.SPAWNPOINT);
        int str = record.getValue(CHARACTERS.STR);
        int dex = record.getValue(CHARACTERS.DEX);
        int intelligence = record.getValue(CHARACTERS.INT);
        int luck = record.getValue(CHARACTERS.LUK);
        int hp = record.getValue(CHARACTERS.HP);
        int mhp = record.getValue(CHARACTERS.MAX_HP);
        int mp = record.getValue(CHARACTERS.MP);
        int mmp = record.getValue(CHARACTERS.MAX_MP);
        int exp = record.getValue(CHARACTERS.EXP);

        Character character = new Character(
                c,
                name,
                charId, gmLevel, level, hair, face,
                gender, skinColor,
                job,
                ap, sp, fame, mapId, spawnpoint,
                str, dex, intelligence, luck,
                hp, mhp, mp, mmp, exp
        );
        character.init();
        return character;
    }

    public static Map<Byte, Integer> getEquips(Character chr) {
        Map<Byte, Integer> equips = new HashMap<>();
        Result<Record2<Byte, Integer>> res = DatabaseCore.getConnection()
                .select(INVENTORIES.POSITION, INVENTORIES.ITEMID)
                .from(INVENTORIES)
                .where(INVENTORIES.CID.eq(chr.getId()))
                .and(INVENTORIES.AID.eq(chr.getClient().getAccId()))
                .and(INVENTORIES.STORAGE_TYPE.eq(1))
                .and(INVENTORIES.INVENTORY_TYPE.eq(-1))
                .fetch();
        for (Record rec : res) {
            equips.put(rec.getValue(INVENTORIES.POSITION), rec.getValue(INVENTORIES.ITEMID));
        }
        return equips;
    }
}
