let status = 0;
let items = [
    4000000,
    4000001,
    4000002,
    4000003
];

function converse(m, s) {
    if (m !== 1) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendSimple(`The stone glows in your presence...#b` +
                `\r\n#L1#Insert item#l` +
                `\r\n#L2#Recall previous knowledge#l`);
        } else if (status === 2) {
            if (s === 1) { // Insert item
                cm.sendSimple(`Choose which item to insert:\r\n#L1#nothing yet#l`);
            } else if (s === 2) { // Recall previous knowledge
                cm.sendOk(``);
            }
        }
    }
}

function buildKnowledgeConvo() {
    let str = `${cm.letters("Knowledge")}\r\n\r\n`;
}