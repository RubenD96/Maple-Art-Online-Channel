/**
 * ADMIN NPC
 * Kenta, gives statted items
 * Maple Art Online
 * @author Chronos
 */

let status = 0;
let stats;
let id;
let keys;
let tempSel;

function converse(m, s) {
    if (!cm.getC().isAdmin()) {
        cm.sendOk("#r#eSwiggity swooty I'm coming for that booty!");
        cm.dispose();
        return;
    }
    if (m !== 1) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendGetNumber(cm.letters("Stat Editor") + "\r\n\r\nProvide a valid equip id:", 1302000, 1000000, 1999999);
        } else if (status === 2) {
            if (id === undefined) {
                id = s;
            }
            if (!cm.isEquip(id)) {
                cm.sendOk(cm.letters("Error") + "\r\n\r\nThe id you entered is not an equip!\r\nYour input was: #r" + id + "\r\n#kPotential item name: #r#z" + id + "#");
                cm.dispose();
                return;
            } else if (stats === undefined) {
                initStats(cm.getEquipById(id));
            }

            let str = "Selected equip: #i" + id + "# #r#z" + id + "# #k(#b" + id + "#k)\r\n";
            for (let i = 0; i < keys.length; i++) {
                str += "#L" + i + "#" + keys[i] + ": #r" + stats[keys[i]] + "#k#l\r\n";
            }
            str += "\r\n#L100##r#eCreate item!#l";
            cm.sendSimple(str);
        } else if (status === 3) {
            if (s !== 100) {
                tempSel = s;
                cm.sendGetNumber(cm.letters(keys[s]) + "\r\n\r\nNew value:", stats[keys[s]], 0, 32767);
            } else {
                cm.gainStatItem(id, stats);
                cm.sendOk("Swiggity swooty I'm coming for that booty!");
                cm.dispose();
            }
        } else if (status === 4) {
            stats[keys[tempSel]] = s;
            status = 1;
            converse(1, 0);
        }
    }
}

function initStats(equip) {
    stats = {
        "STR": equip.getSTR(),
        "DEX": equip.getDEX(),
        "LUK": equip.getLUK(),
        "INT": equip.getINT(),
        "PAD": equip.getPAD(),
        "MAD": equip.getMAD(),
        "ACC": equip.getACC(),
        "EVA": equip.getEVA(),
        "JUMP": equip.getJump(),
        "SPEED": equip.getSpeed(),
        "PDD": equip.getPDD(),
        "MDD": equip.getMDD(),
        "HP": equip.getMaxHP(),
        "MP": equip.getMaxMP(),
        "SLOTS": equip.getRUC()
    };
    keys = Object.keys(stats);
}