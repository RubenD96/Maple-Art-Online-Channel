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
    reward();
    cm.dispose();
}

function reward() {
    if (exp !== 0) cm.gainExp(exp);
    if (col !== 0) cm.gainMeso(col);
    for (let item of items) {
        cm.gainItem(item[0], item[1]);
    }
    cm.completeQuest();
}