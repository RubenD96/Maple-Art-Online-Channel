package net.database

import database.jooq.Tables
import field.`object`.drop.DropEntry
import field.`object`.life.FieldMobTemplate
import managers.MobManager
import net.database.DatabaseCore.connection
import java.util.*

object DropAPI {

    fun getMobDrops(mob: Int): MutableList<DropEntry> {
        val drops = ArrayList<DropEntry>()
        val res = connection.select().from(Tables.MOBDROPS)
                .where(Tables.MOBDROPS.MID.eq(mob))
                .fetch()
        res.forEach {
            drops.add(DropEntry(
                    it.get(Tables.MOBDROPS.IID),
                    it.get(Tables.MOBDROPS.MIN),
                    it.get(Tables.MOBDROPS.MAX),
                    it.get(Tables.MOBDROPS.QUESTID),
                    it.get(Tables.MOBDROPS.CHANCE)
            ))
        }
        return drops
    }

    fun addMobDrop(mid: Int, iid: Int, min: Int, max: Int, questId: Int, chance: Double) {
        val template = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return
        connection.insertInto(Tables.MOBDROPS,
                Tables.MOBDROPS.MID,
                Tables.MOBDROPS.IID,
                Tables.MOBDROPS.MIN,
                Tables.MOBDROPS.MAX,
                Tables.MOBDROPS.QUESTID,
                Tables.MOBDROPS.CHANCE)
                .values(mid,
                        iid,
                        min,
                        max,
                        questId,
                        chance
                ).execute()
        drops.add(DropEntry(iid, min, max, questId, chance))
    }

    fun updateDropChance(mid: Int, iid: Int, chance: Double) {
        val template = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return
        drops.stream().filter { it.id == iid }.forEach { it.chance = chance }
        connection.update(Tables.MOBDROPS)
                .set(Tables.MOBDROPS.CHANCE, chance)
                .where(Tables.MOBDROPS.MID.eq(mid))
                .and(Tables.MOBDROPS.IID.eq(iid))
                .execute()
    }

    fun updateMinMaxChance(mid: Int, iid: Int, min: Int, max: Int, chance: Double) {
        val template: FieldMobTemplate = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return
        drops.stream().filter { it.id == iid }.forEach {
            it.min = min
            it.max = max
            it.chance = chance
        }
        connection.update(Tables.MOBDROPS)
                .set(Tables.MOBDROPS.MIN, min)
                .set(Tables.MOBDROPS.MAX, max)
                .set(Tables.MOBDROPS.CHANCE, chance)
                .where(Tables.MOBDROPS.MID.eq(mid))
                .and(Tables.MOBDROPS.IID.eq(iid))
                .execute()
    }

    fun removeDrop(mid: Int, iid: Int) {
        val template: FieldMobTemplate = MobManager.getMob(mid) ?: return
        val drops = template.drops ?: return
        drops.stream().filter { it.id == iid }.findFirst().ifPresent { drops.remove(it) }
        connection.deleteFrom(Tables.MOBDROPS)
                .where(Tables.MOBDROPS.MID.eq(mid))
                .and(Tables.MOBDROPS.IID.eq(iid))
                .execute()
    }
}

/*
package net.database;

import field.object.drop.DropEntry;
import field.object.life.FieldMobTemplate;
import managers.MobManager;
import org.jooq.Record;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

import static database.jooq.Tables.MOBDROPS;

public class DropAPI {

    public static List<DropEntry> getMobDrops(int mob) {
        List<DropEntry> drops = new ArrayList<>();
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(MOBDROPS)
                .where(MOBDROPS.MID.eq(mob))
                .fetch();

        res.forEach(rec -> drops.add(new DropEntry(
                rec.get(MOBDROPS.IID),
                rec.get(MOBDROPS.MIN),
                rec.get(MOBDROPS.MAX),
                rec.get(MOBDROPS.QUESTID),
                rec.get(MOBDROPS.CHANCE)
        )));

        return drops;
    }

    public static void addMobDrop(int mid, int iid, int min, int max, int questid, double chance) {
        FieldMobTemplate template = MobManager.getMob(mid);
        if (template != null) {
            DatabaseCore.getConnection()
                    .insertInto(MOBDROPS,
                            MOBDROPS.MID,
                            MOBDROPS.IID,
                            MOBDROPS.MIN,
                            MOBDROPS.MAX,
                            MOBDROPS.QUESTID,
                            MOBDROPS.CHANCE)
                    .values(mid,
                            iid,
                            min,
                            max,
                            questid,
                            chance
                    ).execute();

            template.getDrops().add(new DropEntry(iid, min, max, questid, chance));
        }
    }

    public static void updateDropChance(int mid, int iid, double chance) {
        FieldMobTemplate template = MobManager.getMob(mid);
        if (template != null) {
            template.getDrops().stream().filter(drop -> drop.getId() == iid).forEach(drop -> drop.setChance(chance));
        }
        DatabaseCore.getConnection()
                .update(MOBDROPS)
                .set(MOBDROPS.CHANCE, chance)
                .where(MOBDROPS.MID.eq(mid))
                .and(MOBDROPS.IID.eq(iid))
                .execute();
    }

    public static void updateMinMaxChance(int mid, int iid, int min, int max, double chance) {
        FieldMobTemplate template = MobManager.getMob(mid);
        if (template != null) {
            template.getDrops().stream().filter(drop -> drop.getId() == iid).forEach(drop -> {
                drop.setMin(min);
                drop.setMax(max);
                drop.setChance(chance);
            });
        }
        DatabaseCore.getConnection()
                .update(MOBDROPS)
                .set(MOBDROPS.MIN, min)
                .set(MOBDROPS.MAX, max)
                .set(MOBDROPS.CHANCE, chance)
                .where(MOBDROPS.MID.eq(mid))
                .and(MOBDROPS.IID.eq(iid))
                .execute();
    }

    public static void removeDrop(int mid, int iid) {
        FieldMobTemplate template = MobManager.getMob(mid);
        if (template != null) {
            template.getDrops().stream()
                    .filter(drop -> drop.getId() == iid)
                    .findFirst()
                    .ifPresent(entry -> template.getDrops().remove(entry));
        }
        DatabaseCore.getConnection()
                .deleteFrom(MOBDROPS)
                .where(MOBDROPS.MID.eq(mid))
                .and(MOBDROPS.IID.eq(iid))
                .execute();
    }
}

 */