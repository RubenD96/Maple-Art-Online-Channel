execute = () => {
    const args = cs.getArgs();

    if (args.length === 2) {
        let heavy = args[0];
        let delay = args[1];
        cs.tremble(heavy, delay);
    } else {
        cs.tremble(false, 0);
    }
};

execute();