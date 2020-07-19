/**
 * Nolbu, No Questions Asked!
 * Maple Art Online
 * @author Chronos
 */

const exp = 10000;
const col = 20000;
const items = [
    [4032027, -10],
    [4032028, -10],
    [4032029, -10],
    [4032030, -10]
];
let status = 0;

function start(m, s) {
    if (m !== 1) {
        cm.sendOk("Yeah, I'll find somebody else that will not ask questions.");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendAcceptDecline("Hello, I need some items but I don't need your help if you're gonna ask me any questions, deal?");
        } else if (status === 2) {
            cm.sendOk("Good, here is a list of what I need. And remember, #rno questions#k!\r\n\r\n#i4032027# #b10#k #r#t4032027##k\r\n#i4032028# #b10#k #r#t4032028##k\r\n#i4032029# #b10#k #r#t4032029##k\r\n#i4032030# #b10#k #r#t4032030##k");
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
            cm.sendNext("Alright thanks.");
        } else {
            cm.sendOk("Why are you still here?");
            reward();
            cm.dispose();
        }
    }
}

function reward() {
    if (exp !== 0) cm.gainExp(exp);
    if (col !== 0) cm.gainMeso(col);
    for (let item of items) {
        cm.gainItem(item[0], item[1]);
    }
    cm.completeQuest();
}