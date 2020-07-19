execute = () => {
    const args = cs.getArgs();

    if (args.length === 1) {
        cs.openNpc(args[0]);
    }
};

execute();