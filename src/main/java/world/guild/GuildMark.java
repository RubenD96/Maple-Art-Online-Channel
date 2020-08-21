package world.guild;

import lombok.Getter;
import org.jooq.Record;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import static database.jooq.Tables.GUILDMARK;

@Getter
public class GuildMark {

    private final short markBg, mark;
    private final byte markBgColor, markColor;

    public GuildMark(PacketReader reader) {
        markBg = reader.readShort();
        markBgColor = reader.readByte();
        mark = reader.readShort();
        markColor = reader.readByte();
    }

    public GuildMark(Record rec) {
        this.markBg = rec.getValue(GUILDMARK.MARKBG);
        this.mark = rec.getValue(GUILDMARK.MARK);
        this.markBgColor = rec.getValue(GUILDMARK.MARKBGCOLOR);
        this.markColor = rec.getValue(GUILDMARK.MARKCOLOR);
    }

    public GuildMark(short markBg, short mark, byte markBgColor, byte markColor) {
        this.markBg = markBg;
        this.mark = mark;
        this.markBgColor = markBgColor;
        this.markColor = markColor;
    }

    public void encode(PacketWriter pw) {
        pw.writeShort(markBg);
        pw.write(markBgColor);
        pw.writeShort(mark);
        pw.write(markColor);
    }
}
