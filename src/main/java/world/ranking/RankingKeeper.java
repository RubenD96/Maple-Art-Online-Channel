package world.ranking;

import field.object.life.FieldMobTemplate;
import managers.MobManager;
import net.database.RankingAPI;

import java.util.*;
import java.util.stream.Collectors;

public class RankingKeeper {

    public final static RankingKeeper instance = new RankingKeeper();
    private boolean updating = false;

    // rankings
    private Map<String, PlayerRanking> characterData;
    private List<PlayerRanking> regular = new ArrayList<>();
    private List<PlayerRanking> hardcore = new ArrayList<>();
    private List<PlayerRanking> killCount = new ArrayList<>();
    private final Map<Integer, List<PlayerRanking>> mobKills = new LinkedHashMap<>();
    private final Map<Integer, List<PlayerRanking>> bossKills = new LinkedHashMap<>();
    // todo playtime
    // todo mastery
    // todo bosses
    // todo jq's

    // selection lists
    private final Set<Integer> mobs = new HashSet<>();
    private final Set<Integer> bosses = new HashSet<>();

    private RankingKeeper() {
    }

    public static RankingKeeper getInstance() {
        return instance;
    }

    public boolean isUpdating() {
        return updating;
    }

    public Map<String, PlayerRanking> getCharacterData() {
        return characterData;
    }

    public List<PlayerRanking> getRegular() {
        return regular;
    }

    public List<PlayerRanking> getHardcore() {
        return hardcore;
    }

    public List<PlayerRanking> getKillCount() {
        return killCount;
    }

    public Map<Integer, List<PlayerRanking>> getMobKills() {
        return mobKills;
    }

    public Map<Integer, List<PlayerRanking>> getBossKills() {
        return bossKills;
    }

    public Set<Integer> getBosses() {
        return bosses;
    }

    /**
     * For script use
     *
     * @param players List of players to check in
     * @param name    Name to check for
     * @return PlayerRanking object including the rank, or null if it doesn't exist
     */
    public AbstractMap.SimpleEntry<Integer, PlayerRanking> getRankByName(List<PlayerRanking> players, String name) {
        PlayerRanking player = players.stream()
                .filter(playerRanking -> playerRanking.getName().equals(name))
                .findFirst()
                .orElse(null);
        if (player == null) return null;
        Integer index = players.indexOf(player);
        return new AbstractMap.SimpleEntry<>(index, player);
    }

    /**
     * For script use
     *
     * @return A List of mob integers instead of a Set
     */
    public List<Integer> getMobs() {
        return new ArrayList<>(mobs);
    }

    public void updateAllRankings() {
        if (!updating) new Thread(new RankingUpdater()).start();
        else System.err.println("[RankingKeeper] Not done updating yet");
    }

    private class RankingUpdater implements Runnable {

        @Override
        public void run() {
            updating = true;
            characterData = RankingAPI.INSTANCE.getNonGMCharacters();
            updateRegularRanking();
            updateHardcoreRanking();
            updateKillCountRanking();
            updateMobKillsRanking();
            updateBossKillsRanking();
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
        players.forEach(player -> mobs.addAll(player.getMobKills()
                .keySet().stream()
                .filter(mob -> {
                    FieldMobTemplate template = MobManager.getMob(mob);
                    if (template == null) return false;
                    return !template.isBoss();
                }).collect(Collectors.toList())));
        mobs.forEach(mob -> updateMobKillsRanking(mobKills, mob));
    }

    private void updateBossKillsRanking() {
        List<PlayerRanking> players = new ArrayList<>(characterData.values());
        players.forEach(player -> bosses.addAll(player.getMobKills()
                .keySet().stream()
                .filter(mob -> {
                    FieldMobTemplate template = MobManager.getMob(mob);
                    if (template == null) return false;
                    return template.isBoss();
                }).collect(Collectors.toList())));
        bosses.forEach(mob -> updateMobKillsRanking(bossKills, mob));
    }

    private void updateMobKillsRanking(Map<Integer, List<PlayerRanking>> list, int id) {
        List<PlayerRanking> players = characterData.values().stream()
                .filter(playerRanking -> playerRanking.getMobKills().get(id) != null)
                .sorted((p1, p2) -> p2.getMobKills().get(id).compareTo(p1.getMobKills().get(id)))
                .collect(Collectors.toList());
        list.put(id, players); // override old data
    }
}
