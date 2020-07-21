/**
 * Gaga, Gaga's Monster Research Part 2
 * Maple Art Online
 * @author Chronos
 */

const exp = 2000;
const col = 5000;
const items = [
    [2022248, 10]
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("It'll be the end of the world...");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Oh no...");
        } else if (status === 2) {
            cm.sendNext("Oh no!!");
        } else if (status === 3) {
            cm.sendNext("#r#eOh no!!!!");
        } else if (status === 4) {
            cm.sendNext("My research uncovered a hidden location somewhere on Floor 1...");
        } else if (status === 4) {
            cm.sendAcceptDecline("There seems to be a very #e#rdangerous#k#n mob in that map, please take care of it before it reaches this town!");
        } else if (status === 5) {
            cm.startQuest();
            cm.dispose();
        }
    }
}

function end(m, s) {
    cm.sendOk("Wow you've saved this town!\r\nYou're a #bhero#k!!\r\nTake this reward as my eternal gratitude.");
    cm.reward(exp, col, items);
    cm.completeQuest();
    cm.dispose();
}