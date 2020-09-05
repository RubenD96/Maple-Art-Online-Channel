package world.ranking;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerRanking {
    private final int level, job, killCount;
    private final String name;
    private final boolean hardcore, dead;
    private final Map<Integer, Integer> mobKills = new LinkedHashMap<>();

    public PlayerRanking(int level, int job, int killCount, String name, boolean hardcore, boolean dead) {
        this.level = level;
        this.job = job;
        this.killCount = killCount;
        this.name = name;
        this.hardcore = hardcore;
        this.dead = dead;
    }

    public int getLevel() {
        return level;
    }

    public int getJob() {
        return job;
    }

    public int getKillCount() {
        return killCount;
    }

    public String getName() {
        return name;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public boolean isDead() {
        return dead;
    }

    public Map<Integer, Integer> getMobKills() {
        return mobKills;
    }

    @Override
    public String toString() {
        return "PlayerRanking{" +
                "level=" + level +
                ", job=" + job +
                ", killCount=" + killCount +
                ", name='" + name + '\'' +
                ", hardcore=" + hardcore +
                ", dead=" + dead +
                ", mobKills=" + mobKills +
                '}';
    }
}
