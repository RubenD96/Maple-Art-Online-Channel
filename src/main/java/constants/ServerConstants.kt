package constants

import java.util.*

object ServerConstants {

    const val VERSION: Short = 95
    const val CHANNELS = 3
    const val CHANNEL_LOAD = 100
    const val IP = "82.74.154.139"
    const val LOG = true
    const val DEBUG = true // in some cases a different action should be taken during server development in order to prevent some annoying stuff from happening

    const val DB_USER = "root"
    const val DB_PASS = ""
    const val DB_URL = "jdbc:mysql://localhost:3306/mao?serverTimezone=UTC"

    val COMMAND_LIST: ArrayList<ArrayList<String>> = ArrayList()
    val COMMAND_FILE_LIST = HashMap<String, String>()

    const val RANKING_TIMER: Long = 10 /*minutes*/ * 60 * 1000
    const val RESPAWN_TIMER: Long = 5 /*seconds*/ * 1000
    const val DROP_CLEAR_TIMER: Long = 10 /*seconds*/ * 1000
    const val BULK_DUMP_TIMER: Long = 5 /*minutes*/ * 60 * 1000
}