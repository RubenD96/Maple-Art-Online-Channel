execute = () => {
    const args = cs.getArgs();

    if (args.length > 0) {
        let count = args.length === 2 ? args[1] : 1;
        for (let i = 0; i < count; i++) {
            let mob = cs.getMob(args[0]);
            if (mob !== null) {
                let chr = cs.getChr();

                mob.setPosition(chr.getPosition());
                //mob.position = chr.getPosition();
                mob.foothold = chr.getFoothold();
                mob.f = (args.length > 1 ? args[1] : 1) === 1;
                mob.cy = chr.getPosition().y;
                mob.hide = false;
                mob.field = chr.getField();

                cs.getChr().getField().enter(mob);
            }
        }
    }
};

execute();