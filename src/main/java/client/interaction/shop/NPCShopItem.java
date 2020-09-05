package client.interaction.shop;

public class NPCShopItem {

    private final int id;
    private int price, tokenId, tokenPrice, itemPeriod, levelLimited, stock;
    private double unitPrice;
    private short maxPerSlot, quantity;
    private byte discountRate;

    public NPCShopItem(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getTokenPrice() {
        return tokenPrice;
    }

    public void setTokenPrice(int tokenPrice) {
        this.tokenPrice = tokenPrice;
    }

    public int getItemPeriod() {
        return itemPeriod;
    }

    public void setItemPeriod(int itemPeriod) {
        this.itemPeriod = itemPeriod;
    }

    public int getLevelLimited() {
        return levelLimited;
    }

    public void setLevelLimited(int levelLimited) {
        this.levelLimited = levelLimited;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public short getMaxPerSlot() {
        return maxPerSlot;
    }

    public void setMaxPerSlot(short maxPerSlot) {
        this.maxPerSlot = maxPerSlot;
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public byte getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(byte discountRate) {
        this.discountRate = discountRate;
    }
}
