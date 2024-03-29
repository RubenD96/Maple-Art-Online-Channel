/**
 * Filler quest
 * Adahy, Animal-Mushroom stew
 * Maple Art Online
 * @author Chronos
 */

const exp = 1000;
const col = 0;
const items = [
    [4000009, -10],
    [4000001, -10],
    [4000253, -10],
    [4000252, -5],
    [4000017, -3],
    [2010009, -1]
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("May the forest spirits protect you!");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Hello there, do you mind helping me prepare a good meal? I'm starving!", 2);
        } else if (status === 2) {
            cm.sendAcceptDecline("Of course young man, if you give me the proper ingredients I can make you a good stew!");
        } else if (status === 3) {
            cm.sendOk("I'll be making you an Animal-Mushroom stew, please gather the following ingredients from around here:\r\n" +
                "#i4000009# #r10 #b#t4000009#\r\n" +
                "#i4000001# #r10 #b#t4000001#\r\n" +
                "#i4000253# #r10 #b#t4000253#\r\n" +
                "#i4000252# #r5 #b#t4000252#\r\n" +
                "#i4000017# #r3 #b#t4000017#\r\n" +
                "#i2010009# #r1 #b#t2010009#");
            cm.startQuest();
            cm.dispose();
        }
    }
}

function end(m, s) {
    if (m !== 1) {
        cm.sendOk("May the forest spirits protect you!");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("I see you've gotten the ingredients, I'll be making the stew now!");
        } else if (status === 2) {
            cm.sendNext("That was delicious! I feel a lot stronger!", 2);
        } else if (status === 3) {
            cm.sendOk("Of course, that's the power of a good stew.");
            cm.reward(exp, col, items);
            cm.completeQuest();
            cm.dispose();
        }
    }
}