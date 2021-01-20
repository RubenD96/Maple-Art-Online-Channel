/**
 * ADMIN NPC
 * !sense npc script
 * Shows several map objects and their information
 * Maple Art Online
 * @author Chronos
 */

let status = 0, type;
let chr, field;
/* COLLECTIONS */
let npcs, mobs, drops, characters, portals;

function init() {
    if (!cm.getC().isAdmin()) {
        cm.sendOk("Bye :)");
        cm.sendOk("Bye :)");
        return;
    }
    chr = cm.getPlayer();
    field = chr.getField();

    initializeCollections();
}

//CHARACTER, SUMMONED, MOB, NPC, DROP, TOWN_PORTAL, REACTOR, ETC;
function initializeCollections() {
    characters = cm.getCharacters();
    mobs = cm.getMobs();
    npcs = cm.getNpcs();
    drops = cm.getDrops();
    portals = field.getPortals();
}

function converse(m, s) {
    if (m !== 1) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            let str = "The Universe is under no obligation to make sense to you.#b";
            if (!characters.isEmpty())
                str += "\r\n#L1#Players#l";
            if (!mobs.isEmpty())
                str += "\r\n#L3#Monsters#l";
            if (!npcs.isEmpty())
                str += "\r\n#L4#NPCs#l";
            if (!drops.isEmpty())
                str += "\r\n#L5#Dropped items#l";
            if (!portals.isEmpty())
                str += "\r\n#L9#Portals#l";
            cm.sendSimple(str);
        } else if (status === 2) {
            type = s;
            listObjects();
        } else if (status === 3) {
            if (type === 3) {
                cm.getPlayer().setPhilId(cm.getMobByObjId(s).getTemplate().getId());
                cm.openNpcIn(1002000, 200, true);
            } else if (type === 9) {
                cm.sendOk(portals.get(s).toString());
            } else {
                cm.sendOk(field.getGenericObject(s).toString());
            }
            status = 0;
        }
    }
}

function listObjects() {
    let str = "Look up at the stars and not down at your feet. Try to make sense of what you see, and wonder about what makes the universe exist. Be curious.";
    switch (type) {
        case 1: // characters
            str += listCharacters();
            break;
        case 3: // mobs
            str += listMobs();
            break;
        case 4: // npc
            str += listNpcs();
            break;
        case 5: // drops
            str += listDrops();
            break;
        case 9: // portals
            str += listPortals();
            break;
        default:
            cm.sendOk("Something went wrong, please contact a #r#eDeveloper#n#k!");
            return;
    }
    cm.sendSimple(str);
}

function listCharacters() {
    let str = "#b";
    for (let chr of characters) {
        str += "\r\n#L" + chr.getId() + "#" + chr.getName() + " #k(ID: #r" + chr.getId() + "#k LVL: #r" + chr.getLevel() + "#k)#b#l";
    }
    return str;
}

function listMobs() {
    let str = "#b";
    for (let mob of mobs) {
        let stats = mob.getTemplate();
        str += "\r\n#L" + mob.getId() + "#" + stats.getName() + " - " + stats.getId();
        str += "- " + mob.getHp() + "/" + stats.getMaxHP() + " hp";
        str += "#l";
    }
    return str;
}

function listDrops() {
    let str = "#b";
    for (let drop of drops) {
        if (drop.isMeso()) {
            str += "\r\n#L" + drop.getId() + "##e#g" + drop.getInfo() + " Col#k#n#l";
        } else {
            str += "\r\n#L" + drop.getId() + "##r#z" + drop.getInfo() + "##k - #b" + drop.getInfo() + "#k#l";
        }
    }
    return str;
}

function listNpcs() {
    let str = "#b";
    for (let npc of npcs) {
        str += "\r\n#L" + npc.getId() + "#" + npc.getName() + " - " + npc.getNpcId() + "#l";
    }
    return str;
}

function listPortals() {
    let str = "\r\n\r\nThere are a total of: #b" + portals.size() + "#k portals on this map!";
    for (let portal of portals.values()) {
        str += "\r\n#L" + portal.getId() + "##b" + portal.getName() + " - #r#eType: " + portal.getType() + "#k#n";
        str += "\r\n" + portal.toString();
        if (portal.getScript() !== "") {
            str += "\r\n\t\t\tScript: " + portal.getScript();
        }
        if (portal.getTargetMap() !== 999999999) {
            str += "\r\n\t\t\tTm: " + portal.getTargetMap();
        }
        if (portal.getTargetName() !== "") {
            str += "\r\n\t\t\tTp: " + portal.getTargetName();
        }
        str += "#l";
    }
    return str;
}