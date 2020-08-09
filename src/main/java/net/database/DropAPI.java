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
