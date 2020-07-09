execute = () => {
    const args = cs.getArgs();

    if (args.length > 0) {
        cs.getChr().setLevel(args[0]);
    }
}

execute();