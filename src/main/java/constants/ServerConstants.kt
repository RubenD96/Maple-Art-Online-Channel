package constants

import java.util.*

object ServerConstants {

    const val VERSION: Short = 95
    const val CHANNELS = 3
    const val CHANNEL_LOAD = 100
    const val IP = "25.55.234.58"
    const val LOG = true

    const val DB_USER = "root"
    const val DB_PASS = ""
    const val DB_URL = "jdbc:mysql://localhost:3306/mao?serverTimezone=UTC"

    val COMMAND_LIST: ArrayList<ArrayList<String>> = ArrayList()
    val COMMAND_FILE_LIST = HashMap<String, String>()
}

/*
package constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerConstants {

    public static final short VERSION = 95;
    public static final int CHANNELS = 3;
    public static final int CHANNEL_LOAD = 100;
    public static final String IP = "25.55.234.58";
    public static final boolean LOG = true;

    public static final String DB_USER = "root";
    public static final String DB_PASS = "";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/mao?serverTimezone=UTC";

    public static List<List<String>> COMMAND_LIST = new ArrayList<>();
    public static HashMap<String, String> COMMAND_FILE_LIST = new HashMap<>();
}

 */