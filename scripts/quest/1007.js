/**
 * Grandma Yeon, Grandma Yeon's Last Wish
 * Maple Art Online
 * @author Chronos
 */

const exp = 12000;
const col = 4000;
const items = [
    [1112413, 1],
    [4007008, -1]
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("Oh... looks like I'll have to find another warrior to help me fulfill my final wish..");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Hello young warrior. Would you mind helping out an old lady retrieve her lost ring?");
        } else if (status === 2) {
            cm.sendNext("A long time ago when I was younger I went on an expedition with my lover. We found a mystical tree cave east of Tolbana.");
        } else if (status === 3) {
            cm.sendNext("Inside the tree cave we got attacked by mushrooms from all angles, and as we bearly made it out, I lost my ring that day.");
        } else if (status === 4) {
            cm.sendAcceptDecline("It would mean the world to me if you could retrieve my ring from the Zombie Mushmom within the cave!");
        } else if (status === 2) {
            cm.sendOk("Thank you very much for helping me fulfill my final wish!!");
            cm.startQuest();
            cm.dispose();
        }
    }
}

function end(m, s) {
    if (m !== 1) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Is that the ring I see!.. Thank you so much!");
        } else {
            cm.sendOk("I'm at the end of my days. And you fulfilled my wish to see the ring one last time. As a reward I want you to keep the ring!");
            cm.reward(exp, col, items);
            cm.completeQuest();
            cm.dispose();
        }
    }
}