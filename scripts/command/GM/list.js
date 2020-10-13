execute = () => {
    const args = cs.getArgs();

    if (args.length === 1) {
        const type = args[0];
        let inv;
        switch (type) {
            case "equip":
                inv = cs.getPlayer().getInventory(0).getItems();
                break;
            case "use":
                inv = cs.getPlayer().getInventory(1).getItems();
                break;
            case "etc":
                inv = cs.getPlayer().getInventory(2).getItems();
                break;
            case "setup":
                inv = cs.getPlayer().getInventory(3).getItems();
                break;
            case "cash":
                inv = cs.getPlayer().getInventory(4).getItems();
                break;
        }

        let str = "";
        if (inv !== undefined) {
            for (let key of inv.keySet()) {
                const item = inv.get(key).getTemplateId();
                str += "#b" + key + ".#k " + item + " (#r#z" + item + "##k)\r\n";
            }
        }

        cs.sendNpcMessage(str, 22000);
    }
};

execute();