/**
 * RANKING NPC
 * Record Keeper, shows all sort of ranks
 * Maple Art Online
 * @author Chronos
 */

let status = 0;
let name;
let rankings;
let option;
let sel;

const line = "-----------------------";
const options = [
    "Regular",
    "Hardcore",
    "Kill count",
    "Mob kills",
    "Boss kills",
    //"Total playtime rankings",
    //"Mastery rankings"
];

function init() {
    name = cm.getPlayer().getName();
    rankings = cm.getRankings();
}

function converse(m, s) {
    if (rankings.isUpdating()) {
        cm.sendOk("The server is updating the rankings!");
        cm.dispose();
        return;
    }
    if (m !== 1) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            let str = `${cm.letters("Rankings")}\r\n\r\n` +
                `What type of ranking is it you seek?#b`;
            for (const option of options) {
                str += `\r\n#L${options.indexOf(option)}#${option}#l`;
            }
            str += `\r\n\r\n#L100##r#eSearch specific person#l`;
            str += `\r\n#L200#Show myself#l`;
            cm.sendSimple(str);
        } else if (status === 2) {
            sel = -1;
            option = options[s];
            switch (option) {
                case "Regular":
                    showRankings(rankings.getRegular());
                    break;
                case "Hardcore":
                    showRankings(rankings.getHardcore());
                    break;
                case "Kill count":
                    showRankings(rankings.getKillCount());
                    break;
                case "Mob kills":
                    showList(rankings.getMobs(), "Mobs");
                    break;
                case "Boss kills":
                    showList(rankings.getBosses(), "Bosses");
                    break;
            }
        } else if (status === 3) {
            sel = s;
            switch (option) {
                case "Mob kills":
                    showRankings(rankings.getMobKills().get(s));
                    break;
                case "Boss kills":
                    showRankings(rankings.getBossKills().get(s));
                    break;
            }
        }
    }
}

function showRankings(players) {
    let str = ``;
    let showPersonal = true;
    for (let i = 0; i < players.size(); i++) {
        const player = players.get(i);
        if (player.getName() === name) {
            showPersonal = false;
        }
        str += `\r\n` + (i + 1) + `. #b` + player.getName();
        if (player.isDead()) {
            str += ` #k[#e#rDIED#k#n]`;
        } else if (player.isHardcore() && option !== "Hardcore") {
            str += ` #k[#e#rHARDCORE#k#n]`;
        }
        str += `\r\n\t#k${getValueType(player)}#k` + (i !== players.size() - 1 ? (`\r\n` + line) : ``);
    }
    if (showPersonal) {
        let personalRank = rankings.getRankByName(players, name);
        if (personalRank !== null) { // player is in the rankings
            const player = personalRank.getValue();
            str += `\r\n${line}\r\n${personalRank.getKey() + 1}. #b${player.getName()}`;
            str += `\r\n\t${getValueType(player)}`;
        }
    }
    const title = sel === -1 ? cm.letters(option) : cm.letters(cm.getMobTemplate(sel).getName());
    cm.sendOk(`${title}\r\n\r\nThe rankings are as follows:\r\n${str}`);
    status = 0;
}

function getValueType(player) {
    switch (option) {
        case "Regular":
        case "Hardcore":
            return `Lvl. #r${player.getLevel()}#k`;
        case "Kill count":
            return `#r${player.getKillCount()}#k total kills`;
        case "Mob kills":
        case "Boss kills":
            const kills = player.getMobKills().get(sel);
            return `#e#r${kills}#k kill` + (kills === 1 ? "" : "s" + `#n`);
    }
}

function showList(list, title) {
    let str = `#b`;
    for (const item of list) {
        if (title !== "Bosses" || cm.getMobTemplate(item).isBoss()) {
            str += `\r\n#L${item}##o${item}##l`;
        }
    }
    if (str.length === 2) {
        cm.sendOk("Nobody seems to have done anything to be relevant in the rankings yet.");
        cm.dispose();
        return;
    }
    cm.sendSimple(`${cm.letters(title)}\r\n\r\nWhich mob would you like to see the kill rankings for?\r\n${str}`);
}