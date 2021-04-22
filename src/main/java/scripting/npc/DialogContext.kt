package scripting.npc

import client.Client
import client.messages.broadcast.types.AlertMessage
import net.maple.handlers.user.UserChatHandler.Companion.sendMessage
import net.maple.packets.CharacterPackets.message
import net.maple.packets.ConversationPackets

class DialogContext(
    val script: NPCScript,
    val c: Client,
    private val id: Int
) {

    lateinit var holder: StateHolder
    var npcId = id

    var positive: (() -> Unit)? = null
    var neutral: (() -> Unit)? = null
    var negative: (() -> Unit)? = null
    var selections: MutableMap<Int, ((Int) -> Unit)> = HashMap()

    var min: Int = 0
    var max: Int = 0
    var positiveWithNumber: ((Int) -> Unit)? = null

    var positiveWithText: ((String) -> Unit)? = null

    fun sendMessage(
        text: String,
        ok: (() -> Unit)? = null,
        next: (() -> Unit)? = null,
        prev: (() -> Unit)? = null,
        accept: (() -> Unit)? = null,
        decline: (() -> Unit)? = null,
        yes: (() -> Unit)? = null,
        no: (() -> Unit)? = null,
        end: (() -> Unit)? = null,
        speaker: Int = 0 // use SpeakerType object flags!
    ) {
        var positive: (() -> Unit)? = null
        var neutral: (() -> Unit)? = null
        var negative: (() -> Unit)? = null

        ok?.let {
            c.write(ConversationPackets.getOkMessagePacket(npcId, speaker, text))
            positive = ok
        } ?: next?.let {
            prev?.let {
                c.write(ConversationPackets.getNextPrevMessagePacket(npcId, speaker, text))
                neutral = prev
            } ?: run {
                c.write(ConversationPackets.getNextMessagePacket(npcId, speaker, text))
            }
            positive = next
        } ?: prev?.let {
            c.write(ConversationPackets.getPrevMessagePacket(npcId, speaker, text))
            neutral = prev
        } ?: accept?.let {
            c.write(ConversationPackets.getAcceptMessagePacket(npcId, speaker, text))
            positive = accept
            neutral = decline
        } ?: yes?.let {
            c.write(ConversationPackets.getYesNoMessagePacket(npcId, speaker, text))
            positive = yes
            neutral = no
        }

        // if theres no esc, send end chat handler as null
        if (speaker and SpeakerType.NoESC != SpeakerType.NoESC) {
            end?.let {
                negative = it
            }
        }

        this.positive = positive
        this.neutral = neutral
        this.negative = negative
        this.selections.clear()
        this.positiveWithNumber = null
        this.positiveWithText = null
    }

    fun sendSimple(
        text: String,
        appendText: String = "",
        selections: LinkedHashMap<String, ((Int) -> Unit)>,
        end: (() -> Unit)? = null,
        speaker: Int = 0,
        indexes: List<Int>? = null
    ) {
        positive = null
        neutral = end
        negative = null
        positiveWithNumber = null
        positiveWithText = null

        this.selections.clear()
        val npcText = StringBuilder(text)

        indexes?.let {
            selections.entries.forEachIndexed { i, entry ->
                npcText.append("\r\n#L${indexes[i]}#${entry.key}#l")
                this.selections[indexes[i]] = entry.value
            }
        } ?: run {
            selections.entries.forEachIndexed { i, entry ->
                npcText.append("\r\n#L$i#${entry.key}#l")
                this.selections[i] = entry.value
            }
        }

        npcText.append("\r\n\r\n$appendText")

        c.write(ConversationPackets.getSimpleMessagePacket(npcId, speaker, npcText.toString()))
    }

    fun sendGetNumber(
        text: String,
        def: Int,
        min: Int,
        max: Int,
        positive: ((Int) -> Unit),
        end: (() -> Unit)? = null,
        speaker: Int = 0
    ) {
        this.positive = null
        negative = null
        selections.clear()
        positiveWithText = null

        positiveWithNumber = positive
        neutral = end
        this.min = min
        this.max = max

        c.write(ConversationPackets.getNumberMessagePacket(npcId, speaker, text, def, min, max))
    }

    fun sendGetText(
        text: String,
        min: Int,
        max: Int,
        def: String = "",
        positive: ((String) -> Unit),
        end: (() -> Unit)? = null,
        speaker: Int = 0
    ) {
        this.positive = null
        negative = null
        selections.clear()
        positiveWithNumber = null

        positiveWithText = positive
        neutral = end
        this.min = min
        this.max = max

        c.write(ConversationPackets.getTextMessagePacket(npcId, speaker, text, def, min, max))
    }

    fun endMessage(text: String, speaker: Int = SpeakerType.NoESC) {
        c.write(ConversationPackets.getOkMessagePacket(npcId, speaker, text))
        clearStates()
    }

    fun clearStates() {
        positive = null
        neutral = null
        negative = null
        selections.clear()
        c.script = null
    }

    /**
     * WARNING: do not use default values OUTSIDE QuestScript extended classes!!
     */
    fun startQuest(qid: Int = id, npc: Int = npcId) {
        if (qid != npc) {
            c.character.startQuest(qid, npc)
        } else {
            c.character.message(AlertMessage("You should be fired :)"))
        }
    }

    /**
     * WARNING: do not use default values OUTSIDE QuestScript extended classes!!
     */
    fun finishQuest(qid: Int = id) {
        c.character.completeQuest(qid)
    }
}