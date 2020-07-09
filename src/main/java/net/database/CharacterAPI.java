package net.database;

import client.Character;
import client.Client;
import client.player.Job;
import client.player.key.KeyBinding;
import org.jooq.Record;
import org.jooq.Result;

import java.util.HashMap;
import java.util.Map;

import static database.jooq.Tables.CHARACTERS;
import static database.jooq.Tables.KEYBINDINGS;

public class CharacterAPI {

    public static String getOfflineName(int cid) {
        Record rec = DatabaseCore.getConnection()
                .select(CHARACTERS.NAME).from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid)).fetchOne();
        if (rec != null) {
            return rec.getValue(CHARACTERS.NAME);
        }
        return "";
    }

    public static int getOfflineId(String name) {
        Record rec = DatabaseCore.getConnection()
                .select(CHARACTERS.ID).from(CHARACTERS)
                .where(CHARACTERS.NAME.eq(name)).fetchOne();
        if (rec != null) {
            return rec.getValue(CHARACTERS.ID);
        }
        return -1;
    }

    public static Record getOfflineCharacter(int cid) {
        return DatabaseCore.getConnection()
                .select().from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid))
                .fetchOne();
    }

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
        int meso = record.getValue(CHARACTERS.MESO);

        Character character = new Character(
                c,
                name,
                charId, gmLevel, level, hair, face,
                gender, skinColor,
                job,
                ap, sp, fame, mapId, spawnpoint,
                str, dex, intelligence, luck,
                hp, mhp, mp, mmp, exp,
                meso
        );
        character.init();
        return character;
    }

    /**
     * Updates the characters table data except for:
     * - name
     * - gender
     *
     * @param chr character to save
     */
    public static void saveCharacterStats(Character chr) {
        DatabaseCore.getConnection()
                .update(CHARACTERS)
                .set(CHARACTERS.LEVEL, chr.getLevel())
                .set(CHARACTERS.FACE, chr.getFace())
                .set(CHARACTERS.HAIR, chr.getHair())
                .set(CHARACTERS.SKIN, chr.getSkinColor())
                .set(CHARACTERS.JOB, chr.getJob().getId())
                .set(CHARACTERS.AP, chr.getAp())
                .set(CHARACTERS.SP, chr.getSp())
                .set(CHARACTERS.FAME, chr.getFame())
                .set(CHARACTERS.MAP, chr.getField().getId())
                .set(CHARACTERS.SPAWNPOINT, chr.getSpawnpoint())
                .set(CHARACTERS.STR, chr.getStrength())
                .set(CHARACTERS.DEX, chr.getDexterity())
                .set(CHARACTERS.INT, chr.getIntelligence())
                .set(CHARACTERS.LUK, chr.getLuck())
                .set(CHARACTERS.HP, chr.getHealth())
                .set(CHARACTERS.MAX_HP, chr.getMaxHealth())
                .set(CHARACTERS.MP, chr.getMana())
                .set(CHARACTERS.MAX_MP, chr.getMaxMana())
                .set(CHARACTERS.EXP, chr.getExp())
                .set(CHARACTERS.MESO, chr.getMeso())
                .where(CHARACTERS.ID.eq(chr.getId()))
                .execute();
    }

    /**
     * Get bindings that were changed at least once before
     *
     * @param cid character id
     * @return map with KeyBindings and Integer key to bind them to
     */
    public static Map<Integer, KeyBinding> getKeyBindings(int cid) {
        Map<Integer, KeyBinding> keyBindings = new HashMap<>();
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(KEYBINDINGS)
                .where(KEYBINDINGS.CID.eq(cid))
                .fetch();
        for (Record rec : res) {
            keyBindings.put(
                    rec.getValue(KEYBINDINGS.KEY),
                    new KeyBinding(rec.getValue(KEYBINDINGS.TYPE), rec.getValue(KEYBINDINGS.ACTION))
            );
        }

        return keyBindings;
    }

    /**
     * Insert new key bindings, except if binding was already changed before
     *
     * @param chr character
     */
    public static void updateKeyBindings(Character chr) {
        Map<Integer, KeyBinding> keyBindings = chr.getKeyBindings();
        for (Map.Entry<Integer, KeyBinding> keyBinding : keyBindings.entrySet()) {
            DatabaseCore.getConnection()
                    .insertInto(KEYBINDINGS, KEYBINDINGS.CID, KEYBINDINGS.KEY, KEYBINDINGS.TYPE, KEYBINDINGS.ACTION)
                    .values(chr.getId(), keyBinding.getKey(), keyBinding.getValue().getType(), keyBinding.getValue().getAction())
                    .onDuplicateKeyUpdate()
                    .set(KEYBINDINGS.TYPE, keyBinding.getValue().getType())
                    .set(KEYBINDINGS.ACTION, keyBinding.getValue().getAction())
                    .where(KEYBINDINGS.CID.eq(chr.getId()))
                    .and(KEYBINDINGS.KEY.eq(keyBinding.getKey()))
                    .execute();
        }
    }
}
