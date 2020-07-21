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
    cm.reward(exp, col, items);
    cm.completeQuest();
    cm.dispose();
}