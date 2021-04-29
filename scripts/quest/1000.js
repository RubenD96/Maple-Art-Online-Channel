/**
 * Rain, Raining boars
 * Maple Art Online
 * @author Chronos
 */

const exp = 50;
const col = 100;
const items = [
    [4000020, -10]
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        if (status === 1) {
            cm.sendOk("Oh, that's too bad.\nTalk to me again if you change your mind!");
        }
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendAcceptDecline("I want to make a beautiful necklace but I am missing something...\nMaybe you can give me some boar teeth for the necklace? I'll reward you afterwards!");
        } else if (status === 2) {
            cm.sendNext("Wow thanks for doing this, I can't tell you how grateful I am. Please do it quick though, I'm in a hurry!");
            cm.startQuest();
        } else if (status === 3) {
            cm.sendOk("The teeth look like this by the way!\n\n#i4000020# #b10#k #t4000020#");
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
            cm.sendNext("What? You brought the teeth? Okay, let's see...");
        } else if (status === 2) {
            cm.sendOk("Wow thanks a lot, you really made my day! I hope to see you around more often!");
            cm.reward(exp, col, items);
            cm.completeQuest();
            cm.dispose();
        }
    }
}