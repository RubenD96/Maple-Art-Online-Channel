/**
 * Romeo, Curse of the Masks I
 * Maple Art Online
 * @author Chronos
 */

const exp = 10000;
const col = 7500;
const items = [
    [4000196, -100],
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("Ugh..");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendAcceptDecline("Help... me...");
        } else if (status === 2) {
            cm.sendOk("Please... Break... Curse... Bring me #r100 #b#t4000196##k #i4000196#");
            cm.startQuest();
            cm.dispose();
        }
    }
}

function end(m, s) {
    cm.sendOk("Thanks kind... warrior, I... feel a bit... better now\nBut... its not over!");
    cm.reward(exp, col, items);
    cm.completeQuest();
    cm.dispose();
}