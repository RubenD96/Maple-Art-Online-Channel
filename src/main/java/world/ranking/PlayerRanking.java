package world.ranking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class PlayerRanking {
    private final int level, job, killCount;
    private final String name;
    private final boolean hardcore, dead;
    private final Map<Integer, Integer> mobKills = new LinkedHashMap<>();

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
