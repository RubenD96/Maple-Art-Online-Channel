package field.object.drop;

public class DropEntry {

    private int id, min, max, quest;
    private double chance;

    public DropEntry(int id, int min, int max, int quest, double chance) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.quest = quest;
        this.chance = chance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setQuest(int quest) {
        this.quest = quest;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getId() {
        return id;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getQuest() {
        return quest;
    }

    public double getChance() {
        return chance;
    }
}
