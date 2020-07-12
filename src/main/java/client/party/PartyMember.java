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
    private @Setter Character character;

    public PartyMember(Character chr) {
        this.cid = chr.getId();
        this.name = chr.getName();
        this.level = chr.getLevel();
        this.channel = chr.getChannel().getChannelId();
        this.job = chr.getJob().getValue();
        this.field = chr.getFieldId();
        this.online = true;
        this.character = chr;
    }

    public PartyMember() {
        cid = 0;
    }

    public void loadParty(Party party) {
        online = true;
        field = character.getFieldId();
        channel = character.getChannel().getChannelId();

        party.update();
        character.updatePartyHP(true);
    }

    @Override
    public String toString() {
        return "PartyMember{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", channel=" + channel +
                ", job=" + job +
                ", field=" + field +
                ", online=" + online +
                '}';
    }
}
