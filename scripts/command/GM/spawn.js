execute = () => {
    const args = cs.getArgs();

    if (args.length > 0) {
        let count = args.length === 2 ? args[1] : 1;
        for (let i = 0; i < count; i++) {
            let mob = cs.getMob(args[0]);
            if (mob !== null) {
                let chr = cs.getChr();
                mob.setPosition(chr.getPosition());
                mob.setFoothold(chr.getFoothold());
                mob.setF((args.length > 1 ? args[1] : 1) === 1);
                mob.setCy(chr.getPosition().y);
                mob.setHide(false);
                cs.getChr().getField().enter(mob);
            }
        }
    }
};

execute();