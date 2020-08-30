let enabled;
let disabled;
let gender = 0;

function init() {
    enabled = cm.getEnabledHairs(gender);
    disabled = cm.getDisabledHairs(gender);
}

function converse(m, s) {
    if (m !== 1) {
        cm.dispose();
    } else {
        if (s === 0) {
            gender = gender === 0 ? 1 : 0;
            init();
        } else if (s !== -1) {
            const b = getBeauty(s);
            if (enabled.contains(b)) {
                enabled.remove(b);
                disabled.add(b);
            } else {
                enabled.add(b);
                disabled.remove(b);
            }
            cm.updateHair(s);
        }

        let str = "Currently viewing: \r\n#L0##r" + (gender === 0 ? "male" : "female") + "#k#l\r\n\r\n";
        str += "Select a hair to disable:\r\n";
        for (let beauty of enabled) {
            str += "#L" + beauty.getId() + "#" + showHair(beauty.getId()) + "#l";
        }
        str += "\r\n\r\nSelect a hair to enable:\r\n";
        for (let beauty of disabled) {
            str += "#L" + beauty.getId() + "#" + showHair(beauty.getId()) + "#l";
        }
        cm.sendSimple(str);
    }
}

function showHair(hair) {
    return "#fCharacter/Hair/000" + hair + ".img/default/hair#";
}

function getBeauty(id) {
    for (let beauty of enabled) {
        if (beauty.getId() === id) {
            return beauty;
        }
    }
    for (let beauty of disabled) {
        if (beauty.getId() === id) {
            return beauty;
        }
    }
}