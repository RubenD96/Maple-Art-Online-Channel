package client.inventory;

/**
 * Ripped from C# Edelstein
 *
 * @author Kaioru
 */
public enum ItemVariationType {

    NONE(0x00),
    BETTER(0x01),
    NORMAL(0x02),
    GREAT(0x03),
    GACHAPON(0x04);

    private final int type;

    ItemVariationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
