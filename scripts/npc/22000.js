/**
 * Created by Ruben on 7/8/2020.
 */

let status = 0;

function converse(m, s) {
    if (m === -1) {
        cm.sendOk("Bye.");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendOk("Hello #h #");
        } else if (status === 2) {
            cm.sendNext("How are you doing?");
        } else if (status === 3) {
            cm.sendPrev("Please stop clicking ok");
        } else {
            cm.sendNextPrev("I'M BEGGING YOU, STOP");
        }
    }
}