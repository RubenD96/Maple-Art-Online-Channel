package world.guild.bbs

class GuildBBS(val guildId: Int) {

    private var items: MutableList<BBSItem> = ArrayList()
    var high: Int = 0

    fun getNoticeItem(): BBSItem? {
        synchronized(items) {
            return items.firstOrNull { it.id == 0 }
        }
    }

    fun getRegularItems(): List<BBSItem> {
        synchronized(items) {
            return items.filter { it.id != 0 }
        }
    }

    fun getById(id: Int): BBSItem? {
        synchronized(items) {
            return items.firstOrNull { it.id == id }
        }
    }

    fun addItem(cid: Int, title: String, content: String, emote: Int, notice: Boolean = false): BBSItem {
        synchronized(items) {
            var newId = ++high
            if (notice) {
                newId = 0
                high--
            }

            val item = BBSItem(newId, cid, title, content, System.currentTimeMillis(), emote)
            items.add(item)

            return item
        }
    }

    fun removeItem(item: BBSItem) {
        synchronized(items) {
            items.remove(item)
        }
    }

    /**
     * Only to be used once upon loading the guild, see GuildAPI.loadFullBBS
     */
    fun setItems(items: ArrayList<BBSItem>) {
        this.items = items
    }
}