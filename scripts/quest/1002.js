/**
 * Gaga, Gaga's Monster Research Part 1
 * Maple Art Online
 * @author Chronos
 */

const exp = 1500;
const col = 2000;
const items = [

];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        if (status === 1) {
            cm.sendOk("Oh, that's too bad.\r\nTalk to me again if you change your mind!");
        }
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendAcceptDecline("Hello there adventurer! I need some help with my research, I am not very strong so I want you to kill a few mobs for me!");
        } else if (status === 2) {
            cm.sendNext("Thanks for doing this, you will help my research a lot!");
            cm.startQuest();
        } else if (status === 3) {
            cm.sendOk("Please hunt down a few of every mob you find west of Tolbana and come back to me!");
            cm.dispose();
        }
    }
}

function end(m, s) {
    cm.sendOk("Alright now that you have the data, I need to research a bit. Please accept this token of my appreciation");
    cm.reward(exp, col, items);
    cm.completeQuest();
    cm.dispose();
}