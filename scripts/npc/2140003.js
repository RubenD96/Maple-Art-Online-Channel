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
let mastery;

const line = "-----------------------";
const options = [
    "Regular",
    "Hardcore",
    "Kill count",
    "Total damage",
    "Mob kills",
    "Boss kills",
    "Masteries",
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
            mastery = undefined;
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
                case "Total damage":
                    showRankings(rankings.getTotalDamage());
                    break;
                case "Mob kills":
                    showMobList(rankings.getMobs(), "Mobs");
                    break;
                case "Boss kills":
                    showMobList(rankings.getBosses(), "Bosses");
                    break;
                case "Masteries":
                    showMasteryList(rankings.getMasteryTypes(), "Masteries");
                    break;
            }
        } else if (status === 3) {
            switch (option) {
                case "Mob kills":
                    sel = s;
                    showRankings(rankings.getMobKills().get(s));
                    break;
                case "Boss kills":
                    sel = s;
                    showRankings(rankings.getBossKills().get(s));
                    break;
                case "Masteries":
                    mastery = rankings.getMasteryTypes().get(s);
                    showRankings(rankings.getMasteries().get(mastery));
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

    let title;
    if (sel === -1 && mastery === undefined) {
        title = option;
    } else if (mastery === undefined) {
        title = cm.getMobTemplate(sel).getName();
    } else {
        title = mastery.toString();
    }
    cm.sendOk(`${cm.letters(title)}\r\n\r\nThe rankings are as follows:\r\n${str}`);
    status = 0;
}

function getValueType(player) {
    switch (option) {
        case "Regular":
        case "Hardcore":
            return `Lvl. #r${player.getLevel()}#k`;
        case "Kill count":
            return `#r${player.getKillCount()}#k total kills`;
        case "Total damage":
            return `#r${player.getTotalDamage()}#k damage done`;
        case "Mob kills":
        case "Boss kills":
            const kills = player.getMobKills().get(sel);
            return `#e#r${kills}#k kill` + (kills === 1 ? "" : "s" + `#n`);
        case "Masteries":
            return `Lvl. #r${player.getMasteries().get(sel)}#k`;
    }
}

function showMobList(list, title) {
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
    cm.sendSimple(`${cm.letters(title)}\r\n\r\nWhich mob would you like to see the kill rankings for?${str}`);
}

function showMasteryList(list, title) {
    let str = `#b`;
    let i = 0;
    for (const item of list) {
        str += `\r\n#L${i}#${item.toString().toLowerCase()}#l`;
        i++;
    }
    cm.sendSimple(`${cm.letters(title)}\r\n\r\nWhich mastery would you like to see the rankings for?${str}`);
}