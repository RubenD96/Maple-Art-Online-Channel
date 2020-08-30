/**
 * Used to find invalid beauty id's
 */

let ids;
let index = 0;

function init() {
    ids = cm.getAllHairs();
}

function converse(m, s) {
    if (m !== 1) {
        cm.dispose();
    } else {
        console.log("Testing hair: " + ids.get(index));
        cm.sendNext("Index: " + index + "\r\n" +
            "#e#bCurrent hair: " + ids.get(index) + "\r\n\r\n" +
            showHair(ids.get(index).getId()) + "\r\n\r\n" +
            "#rNext hair: " + ids.get(getViableIndex(index + 1)));
        index++;
    }
}

function getViableIndex(i) {
    if (i === ids.size()) {
        index = -1;
        return 0;
    }
    return i;
}

function showHair(hair) {
    return "#fCharacter/Hair/000" + hair + ".img/default/hair#";
}