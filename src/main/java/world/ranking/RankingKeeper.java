package world.ranking;

import lombok.Getter;
import net.database.RankingAPI;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RankingKeeper {

    private @Getter final static RankingKeeper instance = new RankingKeeper();
    private boolean updating = false;

    // rankings
    private Map<String, PlayerRanking> characterData;
    private List<PlayerRanking> regular = new ArrayList<>();
    private List<PlayerRanking> hardcore = new ArrayList<>();
    private List<PlayerRanking> killCount = new ArrayList<>();
    private final Map<Integer, List<PlayerRanking>> mobKills = new LinkedHashMap<>();
    // todo playtime
    // todo mastery
    // todo bosses
    // todo jq's

    // selection lists
    private final Set<Integer> mobs = new HashSet<>();

    private RankingKeeper() {
    }

    public void updateAllRankings() {
        if (!updating) new Thread(new RankingUpdater()).start();
        else System.err.println("[RankingKeeper] Not done updating yet");
    }

    private class RankingUpdater implements Runnable {

        @Override
        public void run() {
            updating = true;
            characterData = RankingAPI.getNonGMCharacters();
            updateRegularRanking();
            updateHardcoreRanking();
            updateKillCountRanking();
            updateMobKillsRanking();
            updating = false;
            System.out.println("[RankingKeeper] All rankings have been updated!");
        }
    }

    private void updateRegularRanking() {
        List<PlayerRanking> players = new ArrayList<>(characterData.values());
        players.sort(Comparator.comparingInt(PlayerRanking::getLevel).reversed());
        regular = players;
    }

    private void updateHardcoreRanking() {
        hardcore = characterData.values().stream()
                .filter(PlayerRanking::isHardcore)
                .sorted(Comparator.comparingInt(PlayerRanking::getLevel).reversed())
                .collect(Collectors.toList());
    }

    private void updateKillCountRanking() {
        List<PlayerRanking> players = new ArrayList<>(characterData.values());
        players.sort(Comparator.comparingInt(PlayerRanking::getKillCount).reversed());
        killCount = players;
    }

    private void updateMobKillsRanking() {
        List<PlayerRanking> players = new ArrayList<>(characterData.values());
        players.forEach(player -> mobs.addAll(player.getMobKills().keySet()));
        mobs.forEach(this::updateMobKillsRanking);
    }

    private void updateMobKillsRanking(int id) {
        List<PlayerRanking> players = new ArrayList<>(characterData.values());
        //players.sort((p1, p2) -> p2.getMobKills().get(id).compareTo(p1.getMobKills().get(id))); caused npe's
        players.sort((p1, p2) -> {
            Integer m1 = p1.getMobKills().get(id);
            if (m1 == null) return 1;
            Integer m2 = p2.getMobKills().get(id);
            if (m2 == null) return -1;
            return m2.compareTo(m1);
        });

        mobKills.put(id, new ArrayList<>()); // override old data
        players.forEach(player -> mobKills.get(id).add(player));
    }
}
