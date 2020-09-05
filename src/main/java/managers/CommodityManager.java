package managers;

import cashshop.commodities.Commodity;
import cashshop.commodities.CommodityFlag;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class CommodityManager extends AbstractManager {

    private static final Map<Integer, Commodity> commodities = new HashMap<>();

    public static synchronized Commodity getCommodity(int sn) {
        Commodity commodity = commodities.get(sn);
        if (commodity == null) {
            commodity = new Commodity(sn);
            if (!loadData(commodity)) {
                return null;
            }
            commodities.put(sn, commodity);
        }
        return commodity;
    }

    private static boolean loadData(Commodity commodity) {
        PacketReader r = getData("wz/Commodity/" + commodity.getSN() + ".mao");
        if (r != null) {
            int flags = r.readInteger();
            r.readInteger(); // SN
            commodity.setItemId(r.readInteger());
            if (containsFlag(flags, CommodityFlag.COUNT)) commodity.setCount(r.readShort());
            if (containsFlag(flags, CommodityFlag.PRIORITY)) commodity.setPriority(r.readByte());
            if (containsFlag(flags, CommodityFlag.PRICE)) commodity.setPrice(r.readInteger());
            if (containsFlag(flags, CommodityFlag.BONUS)) commodity.setBonus(r.readByte());
            if (containsFlag(flags, CommodityFlag.PERIOD)) commodity.setPeriod(r.readShort());
            if (containsFlag(flags, CommodityFlag.REQ_POP)) commodity.setReqPop(r.readShort());
            if (containsFlag(flags, CommodityFlag.REQ_LEV)) commodity.setReqLev(r.readShort());
            if (containsFlag(flags, CommodityFlag.MAPLE_POINT)) commodity.setMaplePoint(r.readInteger());
            if (containsFlag(flags, CommodityFlag.MESO)) commodity.setMeso(r.readInteger());
            if (containsFlag(flags, CommodityFlag.FOR_PREMIUM_USER)) commodity.setForPremiumUser(r.readBool());
            if (containsFlag(flags, CommodityFlag.GENDER)) commodity.setGender(r.readByte());
            if (containsFlag(flags, CommodityFlag.ON_SALE)) commodity.setOnSale(r.readBool());
            if (containsFlag(flags, CommodityFlag.CLASS)) commodity.setJob(r.readByte());
            if (containsFlag(flags, CommodityFlag.LIMIT)) commodity.setLimit(r.readByte());
            if (containsFlag(flags, CommodityFlag.PB_CASH)) commodity.setPbCash(r.readShort());
            if (containsFlag(flags, CommodityFlag.PB_POINT)) commodity.setPbPoint(r.readShort());
            if (containsFlag(flags, CommodityFlag.PB_GIFT)) commodity.setPbGift(r.readShort());

            return true;
        } else {
            System.err.println("Commodity " + commodity.getSN() + " does not exist!");
        }
        return false;
    }

    private static boolean containsFlag(int flags, CommodityFlag flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }
}
