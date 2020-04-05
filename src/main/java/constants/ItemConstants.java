package constants;

public class ItemConstants {

    public static final long PERMANENT = 150841440000000000L;

    public static boolean isRechargeableItem(int templateID) {
        int type = templateID / 10000;
        return type == 207 || type == 233;
    }

    public static boolean isTreatSingly(int templateID) {
        int type = templateID / 1000000;

        if (type == 2 || type == 3 || type == 4) {
            int subType = templateID / 10000;
            return subType == 207 || subType == 233;
        }

        return true;
    }
}
