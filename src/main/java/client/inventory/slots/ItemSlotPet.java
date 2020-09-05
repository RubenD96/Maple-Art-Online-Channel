package client.inventory.slots;

public class ItemSlotPet extends ItemSlot {

    private String petName;
    private byte level, repleteness;
    private short tameness, petAttribute, petSkill, attribute;
    private long dateDead;
    private int remainLife;

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getRepleteness() {
        return repleteness;
    }

    public void setRepleteness(byte repleteness) {
        this.repleteness = repleteness;
    }

    public short getTameness() {
        return tameness;
    }

    public void setTameness(short tameness) {
        this.tameness = tameness;
    }

    public short getPetAttribute() {
        return petAttribute;
    }

    public void setPetAttribute(short petAttribute) {
        this.petAttribute = petAttribute;
    }

    public short getPetSkill() {
        return petSkill;
    }

    public void setPetSkill(short petSkill) {
        this.petSkill = petSkill;
    }

    public short getAttribute() {
        return attribute;
    }

    public void setAttribute(short attribute) {
        this.attribute = attribute;
    }

    public long getDateDead() {
        return dateDead;
    }

    public void setDateDead(long dateDead) {
        this.dateDead = dateDead;
    }

    public int getRemainLife() {
        return remainLife;
    }

    public void setRemainLife(int remainLife) {
        this.remainLife = remainLife;
    }
}
