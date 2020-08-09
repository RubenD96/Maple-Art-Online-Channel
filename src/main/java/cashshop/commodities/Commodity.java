package cashshop.commodities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@RequiredArgsConstructor
public class Commodity {

    private final int SN;

    private int itemId, price, maplePoint, meso;
    private short count, period, reqPop, reqLev, pbCash, pbPoint, pbGift;
    private byte priority, bonus, gender, job, limit;
    private boolean forPremiumUser, onSale;
    private int[] packageSN;

    @Override
    public String toString() {
        return "Commodity{" +
                "SN=" + SN +
                ", itemId=" + itemId +
                ", price=" + price +
                ", maplePoint=" + maplePoint +
                ", meso=" + meso +
                ", count=" + count +
                ", period=" + period +
                ", reqPop=" + reqPop +
                ", reqLev=" + reqLev +
                ", pbCash=" + pbCash +
                ", pbPoint=" + pbPoint +
                ", pbGift=" + pbGift +
                ", priority=" + priority +
                ", bonus=" + bonus +
                ", gender=" + gender +
                ", job=" + job +
                ", limit=" + limit +
                ", forPremiumUser=" + forPremiumUser +
                ", onSale=" + onSale +
                ", packageSN=" + Arrays.toString(packageSN) +
                '}';
    }
}
