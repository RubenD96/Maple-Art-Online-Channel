/**
 * Created by Ruben on 7/8/2020.
 */

let status = 0;

function converse(m, s) {
    if (m !== 1) {
        cm.sendOk("Bye. (" + m + ")");
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendGetTextBox("", 48, 8);
        } else {
            cm.sendOk("hello " + cm.getText());
            cm.dispose();
        }
    }
}