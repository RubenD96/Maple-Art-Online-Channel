/**
 * PLAYER INFO NPC
 * Kirito, multiple functions
 * Maple Art Online
 * @author Chronos
 */

let status = 0;
let tempsel;
let tempsel2;
let hardcore = false;
let skipDispose = false;
let field;

function init() {
    field = cm.getPlayer().getFieldId();
    if (field !== 0 && field !== 2) {
        cm.dispose();
    }
}

function converse(m, s) {
    if (field === 0) {
        explanation(m, s);
    } else if (field === 2) {
        decision(m, s);
    }
}

function explanation(m, s) { // map 0
    if (tempsel === 3 && status === 5 && m !== -1) {
        hardcore = m === 1;
        skipDispose = true;
    }
    if (m !== 1 && !skipDispose) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Hi and welcome to Maple Art Online.");
        } else if (status === 2) {
            cm.sendSimple("Before we continue, I'd like to know about your past experience;#b\r\n" +
                "#L1#I am new to MapleStory#l\r\n" +
                "#L2#I am new to Maple Art Online#l\r\n" +
                "#L3#I am not new to either#l");
        } else if (status === 3) {
            tempsel = s;
            if (s === 1) { // I am new to MapleStory
                cm.sendNext("MapleStory is a 2D side-scrolling MMORPG developed by Wizet and published by Nexon.");
            } else if (s === 2) { // I am new to Maple Art Online
                cm.sendNext("As you might have noticed, this game is quite different from regular MapleStory.\r\n" +
                    "These differences are not just graphical.");
            } else if (s === 3) { // I am not new to either
                cm.sendYesNo("Do you wish to skip the tutorial and go straight to floor 1?");
            }
        } else if (status === 4) {
            if (tempsel === 1) { // I am new to MapleStory
                cm.sendNext("In this game you progress by killing monsters and bosses to level up and equip better gear.\r\n" +
                    "The higher level you are, the more challenging the game becomes!");
            } else if (tempsel === 2) { // I am new to Maple Art Online
                cm.sendNext("Every single mob you will battle has been modified in one way or the other.\r\n" +
                    "There are also many custom maps and a few reworked maps from the original maple.");
            } else if (tempsel === 3) { // Skip tutorial
                cm.sendSimple("Which class would you like to be?\r\n" +
                    "#b#L1#Tank#l\r\n" +
                    "#L2#Swordsman#l\r\n" +
                    "#L3#Spearman#l\r\n\r\n" +
                    "#L4##rPermanent Rookie#k#l");
            }
        } else if (status === 5) {
            if (tempsel === 1) { // I am new to MapleStory
                cm.sendNext("The server you are currently playing on is not the official server, it is custommade by people with no ties to Nexon or Wizet.\r\n" +
                    "Playing on this server can not get you banned on any Nexon/Wizet game.");
            } else if (tempsel === 2) { // I am new to Maple Art Online
                cm.sendNext("Furthermore none of the standard classes from the original maple are playable.\r\n" +
                    "You can currently choose between 3 classes;\r\n" +
                    "#b1.#k Tank\r\n" +
                    "#b2.#k Swordsman\r\n" +
                    "#b3.#k Spearman");
            } else if (tempsel === 3) { // I am not new to either
                tempsel2 = s;
                cm.sendYesNo("Do you wish to make a hardcore character?");
            }
        } else if (status === 6) {
            if (tempsel === 1) { // I am new to MapleStory
                cm.sendNext("Use the arrow keys to move around and alt to jump, if you wish to change certain keys you can open your key configuration by pressing backslash when this chat window is gone.");
            } else if (tempsel === 2) { // I am new to Maple Art Online
                cm.sendNext("The beginner skillbook has been reworked to show your masteries.\r\n" +
                    "Masteries are skills that all jobs have in common, a few examples on masteries are #rAgility#k and #bDivination#k.\r\n\r\n" +
                    "They will be shortly explained on the next page so you get a better understanding of masteries in general");
            } else if (tempsel === 3) { // I am not new to either
                skipDispose = false;
                let job = tempsel2 === 1 ? "Tank" : (tempsel2 === 2 ? "Swordsman" : "Spearman");
                if (tempsel2 === 4) {
                    job = "Permanent Rookie";
                }
                cm.sendYesNo("Are you sure you wish to progress with these settings?\r\n\r\n" +
                    "Job: #e#b" + job + "#k#n\r\n" +
                    "Hardcore: #e" + (hardcore ? "#gOn" : "#rOff"));
            }
        } else if (status === 7) {
            if (tempsel === 1) { // I am new to MapleStory
                cm.sendOk("If you wish to know more about the difference between the official MapleStory servers and this one, talk to me again and select the second option!");
                cm.dispose();
            } else if (tempsel === 2) { // I am new to Maple Art Online
                cm.sendNext("#r#eAgility#n#k\r\n" +
                    "Agility is an active mastery skill that will boost your speed and jump. The higher your agility level is, the faster you will become.\r\n" +
                    "You can level your agility by completing jumpquests.\r\n\r\n" +
                    "#b#eDivination#n#k\r\n" +
                    "Divination is another active mastery skill that can show you what any kind of monster is hiding from you, given your divination level is high enough.\r\n" +
                    "With a high enough divination level, one might see what kind of loot the monster is able to drop and all sorts of other stats like hp and mp.\r\n" +
                    "You can level divination through picking up monster souls, they are dropped from every mob as soon as you learn divination.");
            } else if (tempsel === 3) { // I am not new to either
                starter(tempsel2);
                cm.dispose();
            }
        } else if (status === 8) { // I am new to Maple Art Online
            cm.sendOk("All this information is just the start of your new journey through the maple world!\r\n" +
                "Go towards the portal to learn about all the classes available more in-depth. You will be able to test all 3 Rookie classes!");
            cm.dispose();
        }
    }
}

function decision(m, s) { // map 2
    if (status === 4 && m !== -1) {
        hardcore = m === 1;
        skipDispose = true;
    }
    if (m !== 1 && !skipDispose) {
        cm.dispose();
    } else {
        status++;
        if (status === 1) {
            cm.sendNext("Now that you're here, there is just 1 more thing you need to know!");
        } else if (status === 2) {
            cm.sendNext(cm.letters("Hardcore") + "\r\n\r\nHardcore is a feature which simulates the original Sword Art Online: when enabled, the character only has one life!\r\n" +
                "There are a few exceptions to this rule; if you happen to die in a town or another safe area, you will be able to respawn just like a regular character.\r\n" +
                "There are currently no benefits to be had from playing hardcore. However, hardcores do have a separate ranking.\r\n" +
                "You are able to turn your hardcore character into a regular character at any time if you so desire, however this decision can #enot#n be reversed.");
        } else if (status === 3) {
            cm.sendSimple("Which class would you like to be?\r\n" +
                "#b#L1#Tank#l\r\n" +
                "#L2#Swordsman#l\r\n" +
                "#L3#Spearman#l\r\n\r\n" +
                "#L4##rPermanent Rookie#k#l");
        } else if (status === 4) {
            tempsel = s;
            cm.sendYesNo("Do you wish to make a hardcore character?");
        } else if (status === 5) {
            skipDispose = false;
            let job = tempsel === 1 ? "Tank" : (tempsel === 2 ? "Swordsman" : "Spearman");
            if (tempsel === 4) {
                job = "Permanent Rookie";
            }
            cm.sendYesNo("Are you sure you wish to progress with these settings?\r\n\r\n" +
                "Job: #e#b" + job + "#k#n\r\n" +
                "Hardcore: #e" + (hardcore ? "#gOn" : "#rOff"));
        } else if (status === 6) {
            if (cm.haveItemWithId(1303000, true)) {
                cm.removeAll(1303000);
            }
            if (cm.haveItemWithId(1099010, true)) {
                cm.removeAll(1099010);
            }
            if (cm.haveItemWithId(1402230, true)) {
                cm.removeAll(1402230);
            }
            if (cm.haveItemWithId(1433000, true)) {
                cm.removeAll(1433000);
            }
            cm.getPlayer().resetAllSkills();
            cm.cancelTutorialStats();
            cm.changeJobById(100);
            starter(tempsel);
            dispose();
        }
    }
}

function starter(s) {
    //cm.getPlayer().setHardcore(hardcore);
    cm.gainItem(2000013, 5); // 5 red pots
    cm.gainMeso(100);
    /*if (cm.getPlayer().getRemainingSp() !== 0) {
        cm.getPlayer().gainSp(-cm.getPlayer().getRemainingSp());
    }*/
    cm.getPlayer().heal();
    cm.getPlayer().changeField(1000);
}