package field.object.life;

import field.object.drop.DropEntry;

import java.util.List;

public class FieldMobTemplate {

    private final int id;
    private String name;
    private MoveAbilityType moveType;
    private List<DropEntry> drops;

    private short level;
    private int exp, maxHP, maxMP;
    private boolean boss;

    public FieldMobTemplate(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MoveAbilityType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveAbilityType moveType) {
        this.moveType = moveType;
    }

    public List<DropEntry> getDrops() {
        return drops;
    }

    public void setDrops(List<DropEntry> drops) {
        this.drops = drops;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    @Override
    public String toString() {
        return "FieldMobTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", moveType=" + moveType +
                ", level=" + level +
                ", exp=" + exp +
                ", maxHP=" + maxHP +
                ", maxMP=" + maxMP +
                ", boss=" + boss +
                '}';
    }
}
