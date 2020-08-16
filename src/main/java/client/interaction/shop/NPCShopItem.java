package client.interaction.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NPCShopItem {

    private final int id;
    private int price, tokenId, tokenPrice, itemPeriod, levelLimited, stock;
    private double unitPrice;
    private short maxPerSlot, quantity;
    private byte discountRate;
}
