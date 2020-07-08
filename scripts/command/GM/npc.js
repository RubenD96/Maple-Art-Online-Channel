execute = () => {
    const args = cs.getArgs();

    if (args.length > 0) {
        let npc = cs.getNpc(args[0]);
        let chr = cs.getChr();
        npc.setRx0(chr.getPosition().x + 50);
        npc.setRx1(chr.getPosition().x - 50);
        npc.setPosition(chr.getPosition());
        npc.setFoothold(chr.getFoothold());
        npc.setF((args.length > 1 ? args[1] : 1) === 1);
        npc.setCy(chr.getPosition().y);
        npc.setHide(false);
        cs.getChr().getField().enter(npc);
    }
};

execute();