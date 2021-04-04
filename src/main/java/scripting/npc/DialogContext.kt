package scripting.npc

import client.Client
import net.maple.packets.ConversationPackets

class DialogContext(private val script: NPCScript, private val c: Client, private val id: Int) {

    fun example() {
        sendMessage(
            "Test",
            ok = { },
            speaker = (SpeakerType.NoESC or SpeakerType.NpcReplacedByUser)
        )
    }

    var positive: Runnable? = null
    var neutral: Runnable? = null
    var negative: Runnable? = null
    var finish = false

    fun sendMessage(
        text: String,
        ok: Runnable? = null,
        next: Runnable? = null,
        prev: Runnable? = null,
        accept: Runnable? = null,
        decline: Runnable? = null,
        end: Runnable? = null,
        speaker: Int = 0 // use SpeakerType object flags!
    ) {
        var positive: Runnable? = null
        var neutral: Runnable? = null
        var negative: Runnable? = null

        ok?.let {
            c.write(ConversationPackets.getOkMessagePacket(id, speaker, text))
            positive = ok
        } ?: next?.let {
            prev?.let {
                c.write(ConversationPackets.getNextPrevMessagePacket(id, speaker, text))
                neutral = prev
            } ?: run {
                c.write(ConversationPackets.getNextMessagePacket(id, speaker, text))
            }
            positive = next
        } ?: prev?.let {
            c.write(ConversationPackets.getPrevMessagePacket(id, speaker, text))
            neutral = prev
        } ?: accept?.let {
            c.write(ConversationPackets.getAcceptMessagePacket(id, speaker, text))
            positive = accept
            neutral = decline
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
    }
}