execute = () => {
    let qty = 1;
    let args = cs.getArgs();

    if (args.length > 1) {
        qty = args[1];
    }

    cs.addItem(args[0], qty);
};

execute();