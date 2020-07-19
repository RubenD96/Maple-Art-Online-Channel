/**
 * Rain, Rain's Grudge
 * Maple Art Online
 * @author Chronos
 */

const exp = 500;
const col = 0;
const items = [
    [3010001, 1]
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("That's a shame, I hope you reconsider! The less wolves, the better...");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Hello there, have I ever told you that I truly hate wolves? Well I do.");
        } else if (status === 2) {
            cm.sendAcceptDecline("Please take down #r25#k #o9700040# so I can sleep better tonight!");
        } else if (status === 3) {
            cm.sendOk("After taking down #r25#k #o9700040#, come back to me and I'll reward you.");
            cm.startQuest();
            cm.dispose();
        }
    }
}

function end(m, s) {
    cm.sendOk("Thanks so much for doing this for me!\r\nHere's your reward!");
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