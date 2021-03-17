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
            str += "#L100##r#eCreate item!#l\r\n\r\n#k";
            for (let i = 0; i < keys.length; i++) {
                str += "#L" + i + "#" + keys[i] + ": #r" + stats[keys[i]] + "#k#l\r\n";
            }
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
        "STR": equip.getStr(),
        "DEX": equip.getDex(),
        "LUK": equip.getLuk(),
        "INT": equip.getInt(),
        "PAD": equip.getPad(),
        "MAD": equip.getMad(),
        "ACC": equip.getAcc(),
        "EVA": equip.getEva(),
        "JUMP": equip.getJump(),
        "SPEED": equip.getSpeed(),
        "PDD": equip.getPdd(),
        "MDD": equip.getMdd(),
        "HP": equip.getMaxHP(),
        "MP": equip.getMaxMP(),
        "SLOTS": equip.getRuc(),
        "UPGRADED": 0,
        "STARS": 0,
        "GRADE": 0,
        "LVLUPTYPE": 0,
        "LEVEL": 0,
        "EXP": 0,
        "DURABILITY": 0,
        "OPT1": 0,
        "OPT2": 0,
        "OPT3": 0
    };
    keys = Object.keys(stats);
}