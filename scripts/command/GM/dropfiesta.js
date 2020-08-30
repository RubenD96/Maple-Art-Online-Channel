execute = () => {
    const args = cs.getArgs();
    const mobs = cs.getMobsOnField();

    if (args.length === 1) {
        const numb = args[0];
        if (numb <= 2000) {
            mobs.forEach(mob => {
                for (let i = 0; i < numb; i++) {
                    mob.drop(cs.getPlayer());
                }
            });
        } else {
            cs.sendBlue("Calm down buddy...");
        }
    } else {
        mobs.forEach(mob => {
            mob.drop(cs.getPlayer());
        });
    }
};

execute();