/**
 * ADMIN NPC
 * Phil & Chill, drop changer npc
 * @property of Maple Art Online, please do not distribute without permissions.
 * @author Chronos
 */

let status = 0;
let mobid = 9700041;
let tempsel;
let tempsel2;
let tempselIns;
let drops;
let restart = false;
let item;
let newItem;
let newChance;
let exception = false;
let mesoMin;
let mesoMax;
let mesoChance;
let meso = false;

function init() {
    if (!cm.getC().isAdmin()) {
        cm.sendOk("Bye :)");
        cm.sendOk("Bye :)");
    }
}

function converse(m, s) {
    if (m !== 1 && !exception) {
        cm.sendOk(`Toodaloo #r#eUwU`);
        cm.dispose();
    } else if (m !== 1 && exception && tempselIns === undefined) {
        restartScript();
    } else if (m !== 1 && exception && tempselIns !== undefined) {
        exception = false;
        status = 1;
        converse(1, 2);
    } else {
        status++;
        if (status === 1) {
            sendStartMenu();
        } else if (status === 2) {
            tempsel = s;
            switch (s) {
                case 0: // Change mob id
                    sendChangeMobIdPrompt();
                    break;
                case 1: // Manage drops
                    sendItemList();
                    break;
                case 2: // Add new drop
                    sendNewDropPrompt();
                    break;
                case 3: // Edit meso
                    if (mesoMin === undefined && mesoMax === undefined) {
                        setMesoValues();
                    }
                    sendEditMesoPrompt();
                    break;
            }
        } else if (status === 3) {
            switch (tempsel) {
                case 0: // Change mob id
                    if (mobid !== s) drops = undefined;
                    mobid = s;
                    restartScript();
                    break;
                case 1: // Manage drops
                    if (restart) {
                        restartScript();
                    } else {
                        item = drops.get(s);
                        sendEditDropEntry();
                    }
                    break;
                case 2: // Add new drop
                    tempselIns = s;
                    if (s === 0) { // Change itemid
                        sendChangeItemIdPrompt();
                    } else if (s === 1) { // Change drop chance
                        sendChangeDropChancePrompt();
                    } else if (s === 2) { // Add item
                        exception = true;
                        sendAddItemPrompt();
                    }
                    break;
                case 3: // Edit meso
                    tempselIns = s;
                    if (s === 0) { // change min value
                        sendChangeMesoPrompt(mesoMin, "min");
                    } else if (s === 1) { // change max value
                        sendChangeMesoPrompt(mesoMax, "max");
                    } else if (s === 2) { // change drop chance
                        sendChangeMesoChancePrompt();
                    } else if (s === 3) { // apply changes
                        if (mesoMin <= mesoMax) {
                            sendApplyMesoChanges();
                        } else {
                            cm.sendOk("Minimum can't be higher than maximum");
                        }
                    }
                    break;
            }
        } else if (status === 4) {
            tempsel2 = s;
            switch (tempsel) {
                case 1: // manage drops
                    if (s === 0) { // edit chance
                        sendEditDropChance();
                    } else if (s === 1) { // remove item
                        exception = true;
                        sendDeleteDrop();
                    }
                    break;
                case 2: // Add new drop
                    if (tempselIns === 0) { // Change itemid
                        newItem = s;
                        status = 1;
                        converse(m, 2);
                    } else if (tempselIns === 1) { // Change drop chance
                        newChance = s;
                        status = 1;
                        converse(m, 2);
                    } else if (tempselIns === 2) { // Add item
                        insertItem();
                    }
                    break;
                case 3:
                    if (tempselIns === 0) { // change min value
                        mesoMin = s;
                    } else if (tempselIns === 1) { // change min value
                        mesoMax = s;
                    } else if (tempselIns === 2) { // change meso chance
                        mesoChance = s;
                    }
                    status = 1;
                    converse(m, 3);
                    break;
            }
        } else if (status === 5) {
            switch (tempsel) {
                case 1: // manage drops
                    if (tempsel2 === 0) { // edit chance
                        if (s === item.getChance()) {
                            cm.sendOk(cm.letters("Failed") + `\r\nNo changes were made as the new chance was the same as the old chance.`);
                        } else {
                            updateDropChance(s);
                        }
                    } else if (tempsel2 === 1) { // remove item
                        deleteDrop();
                    }
                    break;
                case 2: // insert
                    restartScript();
            }
        } else if (status === 6) {
            restartScript();
        }
    }
}

function sendStartMenu() {
    cm.sendSimple(cm.letters("Phil n Chill") + `
    \r\nCurrent mob: #b#o${mobid}##k (#r${mobid}#k)
    \r\nIf the mobname does not show up it probably means the mobdoes not exist!
    \r\n#b#L0#Change mob id#l
    \r\n#L1#Manage drops#l
    \r\n#L2#Add new drop#l
    \r\n#L3#Edit meso drop#l`);
}

function sendChangeMobIdPrompt() {
    cm.sendGetNumber(cm.letters("Change Mob") + `
    \r\nCurrent mob: #b#o${mobid}##k (#r${mobid}#k)
    \r\nnew mob id:`, mobid, 100100, 9999999);
}

function sendItemList() {
    drops = cm.getMobDrops(mobid);
    let str = cm.letters("Manage Drops") + `
    \r\nAll items #b#o${mobid}##k (#r${mobid}#k) drops:`;
    if (drops.length === 0) {
        str += `\r\nThis mob does not drop anything!`;
        cm.sendOk(str);
        restart = true;
    } else {
        let i = 0;
        for (let drop of drops) {
            let id = drop.getId();
            if (id !== 0) { // meso
                let chance = 1 / drop.getChance() * 100;
                str += `\r\n#L${i++}##i${id}# #b#z${id}##k ${chance}%#l`
            }
        }
        cm.sendSimple(str);
    }
}

function sendEditDropEntry() {
    let itemid = item.getId();
    let chance = 1 / item.getChance() * 100;
    cm.sendSimple(cm.letters("Edit Item") + `
    \r\nYou've selected: #i${itemid}# #b#t${itemid}#
    \r\nThe current drop chance for this item is: #r${chance}%#k.
    \r\nWhat would you like to do?
    \r\n#b#L0#Change drop-chance on this item#l
    \r\n#L1#Remove this item from monster's droplist#l`);
}

function sendEditDropChance() {
    let itemid = item.getId();
    let chance = 1 / item.getChance() * 100;
    cm.sendGetNumber(cm.letters("Change Chance") + `
    \r\nYou've selected: #i${itemid}# #b#t${itemid}##k.
    \r\nThe current drop chance for this item is: #r${chance}%#k.
    \r\nNew drop chance (1 / chance (eg current is 1/${item.getChance()} so ${chance}%)):`,
        item.getChance(), 1, 1000000);
}

function sendDeleteDrop() {
    let itemid = item.getId();
    cm.sendYesNo(cm.letters("Delete Item") + `
    \r\n\r\nAre you sure you want to delete #i${itemid}# #b#t${itemid}##k?`);
}

function updateDropChance(newChance) {
    let itemid = item.getId();
    cm.editDropChance(mobid, itemid, newChance);
    cm.sendOk(cm.letters("Success") + `
    \r\n\r\n#i${itemid}# drop chance is changed to #r${1 / newChance * 100}%#k for #d#o${mobid}#`);
}

function deleteDrop() {
    let itemid = item.getId();
    cm.removeDrop(mobid, itemid);
    cm.sendOk(cm.letters("Deleted") + `
    \r\n#i${itemid}# #b#t${itemid}##k has been removed!`);
}

function sendNewDropPrompt() {
    cm.sendSimple(cm.letters("Add Item") + `
    \r\nCurrent mob: #b#o${mobid}##k (#r${mobid}#k)
    \r\nCurrent item to add: #b` + (newItem === undefined ? `not set yet!#k` : `#i${newItem}##k (#r${newItem}#k)`) + `
    \r\nDrop chance for item to add: #r` + (newChance === undefined ? `not set yet!` : `${1 / newChance * 100}%`) + `#k
    \r\n#b#L0#Change itemid#l
    \r\n#L1#Change drop chance#l
    \r\n#L2#Add item#l`);
}

function sendChangeItemIdPrompt() {
    cm.sendGetNumber(cm.letters("Change Item") + `
    \r\nCurrent item to add: #b` + (newItem === undefined ? `not set yet!#k` : `#i${newItem}##k (#r${newItem}#k)`) + `
    \r\nnew itemID:`, (newItem === undefined ? 4000000 : newItem), 1000000, 9999999);
}

function sendChangeDropChancePrompt() {
    cm.sendGetNumber(cm.letters("Change Chance") + `
    \r\nCurrent item to add: #b` + (newItem === undefined ? `not set yet!#k` : `#i${newItem}##k (#r${newItem}#k)`) + `
    \r\nDrop chance for item to add: #r` + (newChance === undefined ? `not set yet!` : (1 / newChance * 100 + `%`)) + `
    \r\nNew drop chance (1 / chance):`,
        (newChance === undefined ? 1 : newChance), 1, 1000000);
}

function sendAddItemPrompt() {
    if (newItem === undefined || newChance === undefined) {
        cm.sendPrev(`You have not provided enough data to add an item.`, 1);
    } else {
        cm.sendYesNo(`Are you sure you want to add #b#i${newItem}##k (#r${newItem}#k) with a drop chance of #r${1 / newChance * 100}%#k into: #b#o${mobid}##k?`);
    }
}

function insertItem() {
    cm.addMobDrop(mobid, newItem, newChance);
    cm.sendOk(`#i${newItem}# #b#t${newItem}##k has been added to #d#o${mobid}##k with a drop chance of #r${1 / newChance * 100}%#k.`);
}

function setMesoValues() {
    let all = cm.getMobDrops(mobid);
    for (let drop of all) {
        if (drop.getId() === 0) {
            mesoMin = drop.getMin();
            mesoMax = drop.getMax();
            mesoChance = drop.getChance();
            meso = true;
            break;
        }
    }
}

function sendEditMesoPrompt() {
    cm.sendSimple(cm.letters("Mesos") + `
    \r\nCurrent mob: #b#o${mobid}##k (#r${mobid}#k)
    \r\nMin meso: #b` + (mesoMin === undefined ? `not set yet!` : mesoMin) + `#k
    \r\nMax meso: #b` + (mesoMax === undefined ? `not set yet!` : mesoMax) + `#k
    \r\nChance to drop: #r` + (mesoChance === undefined ? `not set yet!` : 1 / mesoChance * 100 + `%`) + `#b
    \r\n#L0#Change min value#l
    \r\n#L1#Change max value#l
    \r\n#L2#Change drop chance#l
    \r\n#L3#Apply changes#l`);
}

function sendChangeMesoPrompt(current, str) {
    cm.sendGetNumber(cm.letters(str === "max" ? "Maximum" : "Minimum") + `
    \r\nCurrent ${str} meso: #b` + (current === undefined ? `not set yet!` : current) + `#k
    \r\nNew min value:`, current === undefined ? 1 : current, 1, 2147483647);
}

function sendChangeMesoChancePrompt() {
    cm.sendGetNumber(cm.letters("Drop Chance") + `
    \r\nChance to drop: #r` + (mesoChance === undefined ? "not set yet!" : (1 / mesoChance * 100) + `%`) + `#k
    \r\nNew drop chance (1 / chance):`,
        mesoChance ? 1 : mesoChance, 1, 10000);
}

function sendApplyMesoChanges() {
    if (meso) {
        cm.editMinMaxChance(mobid, 0, mesoMin, mesoMax, mesoChance);
    } else {
        cm.addMobDrop(mobid, 0, mesoMin, mesoMax, 0, mesoChance);
    }
    cm.sendOk(`Meso drop (${mesoMin} - ${mesoMax}) has been ` + (meso ? `changed for` : `added to`) + ` #d#o${mobid}##k with a drop chance of #r${1 / mesoChance * 100}%#k.`);
}

function restartScript() {
    exception = false;
    newItem = undefined;
    newChance = undefined;
    status = 0;
    tempsel = 0;
    tempsel2 = 0;
    converse(1, 0);
}