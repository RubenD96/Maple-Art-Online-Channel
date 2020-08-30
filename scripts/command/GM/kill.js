execute = () => {
    const args = cs.getArgs();

    if (args.length > 1) {
        switch (args[0]) {
            case "mob":
            case "mobs":
                const mobs = cs.getMobsOnField();
                if (args[1] === "all") {
                    mobs.forEach(mob => {
                        mob.kill(cs.getPlayer());
                    });
                }
                break;
        }
    } else {
        cs.sendBlue(`!kill <player(s)/mob(s)> <name/all>`);
    }
};

execute();