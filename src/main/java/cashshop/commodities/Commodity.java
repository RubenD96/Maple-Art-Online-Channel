package cashshop.commodities;

import java.util.Arrays;

public class Commodity {

    private final int SN;

    public Commodity(int SN) {
        this.SN = SN;
    }

    private int itemId, price, maplePoint, meso;
    private short count, period, reqPop, reqLev, pbCash, pbPoint, pbGift;
    private byte priority, bonus, gender, job, limit;
    private boolean forPremiumUser, onSale;
    private int[] packageSN;

    public int getSN() {
        return SN;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMaplePoint() {
        return maplePoint;
    }

    public void setMaplePoint(int maplePoint) {
        this.maplePoint = maplePoint;
    }

    public int getMeso() {
        return meso;
    }

    public void setMeso(int meso) {
        this.meso = meso;
    }

    public short getCount() {
        return count;
    }

    public void setCount(short count) {
        this.count = count;
    }

    public short getPeriod() {
        return period;
    }

    public void setPeriod(short period) {
        this.period = period;
    }

    public short getReqPop() {
        return reqPop;
    }

    public void setReqPop(short reqPop) {
        this.reqPop = reqPop;
    }

    public short getReqLev() {
        return reqLev;
    }

    public void setReqLev(short reqLev) {
        this.reqLev = reqLev;
    }

    public short getPbCash() {
        return pbCash;
    }

    public void setPbCash(short pbCash) {
        this.pbCash = pbCash;
    }

    public short getPbPoint() {
        return pbPoint;
    }

    public void setPbPoint(short pbPoint) {
        this.pbPoint = pbPoint;
    }

    public short getPbGift() {
        return pbGift;
    }

    public void setPbGift(short pbGift) {
        this.pbGift = pbGift;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public byte getBonus() {
        return bonus;
    }

    public void setBonus(byte bonus) {
        this.bonus = bonus;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public byte getJob() {
        return job;
    }

    public void setJob(byte job) {
        this.job = job;
    }

    public byte getLimit() {
        return limit;
    }

    public void setLimit(byte limit) {
        this.limit = limit;
    }

    public boolean isForPremiumUser() {
        return forPremiumUser;
    }

    public void setForPremiumUser(boolean forPremiumUser) {
        this.forPremiumUser = forPremiumUser;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public int[] getPackageSN() {
        return packageSN;
    }

    public void setPackageSN(int[] packageSN) {
        this.packageSN = packageSN;
    }

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
