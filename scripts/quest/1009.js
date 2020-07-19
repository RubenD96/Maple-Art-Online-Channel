/**
 * Romeo, Curse of the Masks II
 * Maple Art Online
 * @author Chronos
 */

const exp = 15000;
const col = 12500;
const items = [
    [4000197, -100],
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("Ugh..");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendAcceptDecline("Please help... me one more... time!");
        } else if (status === 2) {
            cm.sendOk("The curse... its not fully... gone, bring me #r100 #b#t4000197##k #i4000197#");
            cm.startQuest();
            cm.dispose();
        }
    }
}

function end(m, s) {
    cm.sendOk("Wow the curse... I cant believe it! The curse is gone!");
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