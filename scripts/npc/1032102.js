/**
 * WARP NPC
 * Luna, portal master
 * Maple Art Online
 * @author Chronos
 */

let destinations = [];
let status = 0;

function init() {
    destinations = getAvailableDestinations();
}

function converse(m, s) {
    /*if (cm.getPlayer().getCustomQuestStatus(12000) === 2) {
        const timeleft = (cm.getPlayer().getCustomQuest(12000).getEndTime() - cm.getCurrentTime()) / 1000;
        cm.sendOk("Uh oh! Looks like we're having some problems with the town portals...\r\nThey should be up and running in exactly " + timeleft + " seconds!");
        cm.dispose();
        return;
    }*/

    if (m === -1) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            let text = "Hi I'm #bLuna#k.\r\nYou can use the #rtown portals#k to move to other #rtowns#k, but you need to have #rvisited#k the town before unlocking its portal.";
            if (destinations.length !== 0) {
                text += "\r\n\r\nWhere do you want to go?#b";
                for (let i = 0; i < destinations.length; i++) {
                    text += "\r\n#L" + i + "##m" + destinations[i] + "##l";
                }
                cm.sendSimple(text);
            } else {
                cm.sendOk(text);
                cm.dispose();
            }
        } else if (status === 2) {
            if (m === 1) {
                cm.warp(destinations[s], "portal");
            }
            cm.dispose();
        }
    }
}

function getAvailableDestinations() {
    let towns = cm.getPlayer().getTowns();
    let ret = [];
    for (let town of towns) {
        if (town !== cm.getMapId()) {
            ret.push(town);
        }
    }
    return ret;
}