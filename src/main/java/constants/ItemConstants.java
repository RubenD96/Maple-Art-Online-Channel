package constants;

public class ItemConstants {

    public static final long PERMANENT = 150841440000000000L;

    public static boolean isRechargeableItem(int templateID) {
        var type = templateID / 10000;
        return type == 207 || type == 233;
    }
}
