let status = 0;
let cost = 1500000;

function converse(m, s) {
    if (m !== 1) {
        cm.dispose();
    } else {
        status++;
        if (cm.getGuild() == null) {
            noGuildConvo(s);
        } else {
            guildConvo(s);
        }
    }
}

function noGuildConvo(s) {
    if (status === 1) {
        cm.sendSimple(cm.letters("Guild") + "\r\n\r\n" +
            "Young padawan, how u doin?#b" +
            "\r\n#L0#Info#l" +
            "\r\n#L1#Create#l");
    } else if (status === 2) {
        if (s === 0) { // info
            cm.sendOk(cm.letters("Info") + "\r\n\r\n" +
                "#eCreation cost#n\r\n" +
                "+ To create a guild, you'll need #r" + cost + "#k mesos.\r\n\r\n" +
                "#eNaming#n\r\n" +
                "+ All guild names must be #bunique#k.\r\n" +
                "+ Guild names can be anywhere between #r3#k and #r50#k characters.\r\n" +
                "+ Guild names #bmay#k contain spaces.");
        }
    }
}

function guildConvo(s) {
    if (status === 1) {
        cm.sendSimple(cm.letters("Guild") + "\r\n\r\n" +
            "Young padawan, how u doin?#b" +
            "\r\n#L0#Expand#l" +
            "\r\n#L1#Disband#l");
    }
}