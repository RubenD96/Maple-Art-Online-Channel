package client.party;

import client.Character;

public class PartyMember {

    private final int cid;
    private String name = "";
    private int level, channel = -2, job, field;
    private boolean online;
    private Character character;

    public int getCid() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

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
