package client.party;

import client.Character;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PartyMember {

    private final int cid;
    private @Setter String name = "";
    private @Setter int level, channel = -2, job, field;
    private @Setter boolean online;

    public PartyMember(Character chr) {
        this.cid = chr.getId();
        this.name = chr.getName();
        this.level = chr.getLevel();
        this.channel = chr.getChannel().getChannelId();
        this.job = chr.getJob().getValue();
        this.field = chr.getFieldId();
        this.online = true;
    }

    public PartyMember() {
        cid = 0;
    }
}
