package constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerConstants {

    public static final short VERSION = 95;
    public static final int CHANNELS = 3;
    public static final int CHANNEL_LOAD = 100;
    public static final String IP = "25.55.234.58";
    public static final int LOGIN_PORT = 8484;
    public static final boolean LOG = true;

    public static final String DB_USER = "root";
    public static final String DB_PASS = "";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/mao?serverTimezone=UTC";

    public static List<List<String>> COMMAND_LIST = new ArrayList<>();
    public static HashMap<String, String> COMMAND_FILE_LIST = new HashMap<String, String>();
}
